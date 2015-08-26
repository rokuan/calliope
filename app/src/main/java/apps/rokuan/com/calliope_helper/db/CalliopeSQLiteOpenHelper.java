package apps.rokuan.com.calliope_helper.db;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.rokuan.calliopecore.parser.SpeechParser;
import com.rokuan.calliopecore.parser.WordDatabase;
import com.rokuan.calliopecore.sentence.Action;
import com.rokuan.calliopecore.sentence.CityInfo;
import com.rokuan.calliopecore.sentence.ColorInfo;
import com.rokuan.calliopecore.sentence.CountryInfo;
import com.rokuan.calliopecore.sentence.CustomMode;
import com.rokuan.calliopecore.sentence.CustomObject;
import com.rokuan.calliopecore.sentence.CustomPerson;
import com.rokuan.calliopecore.sentence.CustomPlace;
import com.rokuan.calliopecore.sentence.LanguageInfo;
import com.rokuan.calliopecore.sentence.PlacePreposition;
import com.rokuan.calliopecore.sentence.PurposePreposition;
import com.rokuan.calliopecore.sentence.TimePreposition;
import com.rokuan.calliopecore.sentence.Verb;
import com.rokuan.calliopecore.sentence.VerbConjugation;
import com.rokuan.calliopecore.sentence.WayPreposition;
import com.rokuan.calliopecore.sentence.Word;
import com.rokuan.calliopecore.sentence.structure.InterpretationObject;
import com.rokuan.calliopecore.sentence.structure.data.CountConverter;
import com.rokuan.calliopecore.sentence.structure.data.DateConverter;
import com.rokuan.calliopecore.sentence.structure.data.place.PlaceAdverbial;
import com.rokuan.calliopecore.sentence.structure.data.purpose.PurposeAdverbial;
import com.rokuan.calliopecore.sentence.structure.data.time.TimeAdverbial;
import com.rokuan.calliopecore.sentence.structure.data.way.WayAdverbial;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import apps.rokuan.com.calliope_helper.event.ProfileEvent;
import de.greenrobot.event.EventBus;

/**
 * Created by LEBEAU Christophe on 18/07/15.
 */
public class CalliopeSQLiteOpenHelper extends OrmLiteSqliteOpenHelper implements WordDatabase {
    public static final String DATA_SEPARATOR = ";";
    public static final String SPECIAL_ENCODING = "ISO-8859-1";

    private static final Class<?>[] COMMON_CLASSES = {
            Word.class,
            VerbConjugation.class,
            PlacePreposition.class,
            TimePreposition.class,
            WayPreposition.class,
            PurposePreposition.class,
            LanguageInfo.class,
            ColorInfo.class,
            CityInfo.class,
            CountryInfo.class
    };
    private static final String[] COMMON_COLUMN_NAMES = {
            // Ordre different pour des raisons de performance
            Word.WORD_FIELD_NAME,
            VerbConjugation.VALUE_FIELD_NAME,
            PlacePreposition.VALUE_FIELD_NAME,
            TimePreposition.VALUE_FIELD_NAME,
            WayPreposition.VALUE_FIELD_NAME,
            PurposePreposition.VALUE_FIELD_NAME,
            LanguageInfo.LANGUAGE_FIELD_NAME,
            ColorInfo.COLOR_FIELD_NAME,
            CityInfo.CITY_FIELD_NAME,
            CountryInfo.COUNTRY_FIELD_NAME
    };
    private static final Class<?>[] PROFILE_CLASSES = {
            CustomProfileObject.class,
            CustomProfilePlace.class,
            CustomProfilePerson.class,
            CustomProfileMode.class
    };
    private static final String[] PROFILE_RELATED_COLUMN_NAMES = {
            CustomObject.OBJECT_FIELD_NAME,
            CustomPlace.PLACE_FIELD_NAME,
            CustomPerson.PERSON_FIELD_NAME,
            CustomMode.MODE_FIELD_NAME
    };

    private static final String DB_NAME = "calliope_helper";
    private static final int DB_VERSION = 1;
    private Context context;
    private EventBus defaultBus = EventBus.getDefault();
    private SpeechParser parser;

    public CalliopeSQLiteOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
        parser = new SpeechParser(this);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Profile.class);

            // TODO: tables des et prenoms ?
            TableUtils.createTable(connectionSource, Word.class);
            TableUtils.createTable(connectionSource, CityInfo.class);
            TableUtils.createTable(connectionSource, CountryInfo.class);
            TableUtils.createTable(connectionSource, LanguageInfo.class);
            TableUtils.createTable(connectionSource, ColorInfo.class);
            TableUtils.createTable(connectionSource, Verb.class);
            TableUtils.createTable(connectionSource, VerbConjugation.class);
            TableUtils.createTable(connectionSource, PlacePreposition.class);
            TableUtils.createTable(connectionSource, TimePreposition.class);
            TableUtils.createTable(connectionSource, WayPreposition.class);
            TableUtils.createTable(connectionSource, PurposePreposition.class);

            TableUtils.createTable(connectionSource, CustomProfileObject.class);
            TableUtils.createTable(connectionSource, CustomProfilePlace.class);
            TableUtils.createTable(connectionSource, CustomProfilePerson.class);
            TableUtils.createTable(connectionSource, CustomProfileMode.class);

            defaultBus.post(new DatabaseEvent("Profils"));
            loadProfiles(connectionSource);
            defaultBus.post(new DatabaseEvent("Mots"));
            loadWords(connectionSource);
            defaultBus.post(new DatabaseEvent("Villes"));
            loadCities(connectionSource);
            defaultBus.post(new DatabaseEvent("Pays"));
            loadCountries(connectionSource);
            defaultBus.post(new DatabaseEvent("Langues"));
            loadLanguages(connectionSource);
            defaultBus.post(new DatabaseEvent("Couleurs"));
            loadColors(connectionSource);
            defaultBus.post(new DatabaseEvent("Verbes"));
            loadVerbs(connectionSource);
            defaultBus.post(new DatabaseEvent("Conjugaisons"));
            loadConjugations(connectionSource);
            defaultBus.post(new DatabaseEvent("Autres"));
            loadPlacePrepositions(connectionSource);
            loadTimePrepositions(connectionSource);
            loadWayPrepositions(connectionSource);
            loadPurposePrepositions(connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        // TODO:
    }

    private void loadProfiles(ConnectionSource connectionSource) throws SQLException {
        Profile defaultProfile = new Profile(Profile.DEFAULT_PROFILE_CODE, "Defaut");
        Dao<Profile, String> profileDao = DaoManager.createDao(connectionSource, Profile.class);

        profileDao.create(defaultProfile);
        context.getSharedPreferences(Profile.PROFILE_PREF_KEY, 0).edit()
                .putString(Profile.ACTIVE_PROFILE_KEY, Profile.DEFAULT_PROFILE_CODE)
                .apply();
    }

    private void loadWords(ConnectionSource connectionSource) throws IOException, SQLException {
        AssetManager assets = context.getAssets();
        InputStream in = assets.open("words.txt");
        Scanner sc = new Scanner(in);
        Dao<Word, Integer> dao = DaoManager.createDao(connectionSource, Word.class);

        while(sc.hasNextLine()){
            String line = sc.nextLine();
            String[] fields = line.split(DATA_SEPARATOR);
            String[] types = fields[1].split(",");
            Set<Word.WordType> wordTypes = new HashSet<>();

            for(String ty: types){
                wordTypes.add(Word.WordType.valueOf(ty));
            }

            dao.create(new Word(fields[0], wordTypes));
        }

        in.close();
        sc.close();
    }

    private void loadCities(ConnectionSource connectionSource) throws IOException, SQLException {
        AssetManager assets = context.getAssets();
        InputStream in = assets.open("cities.txt");
        Scanner sc = new Scanner(in);
        Dao<CityInfo, Integer> dao = DaoManager.createDao(connectionSource, CityInfo.class);

        while(sc.hasNextLine()){
            String line = sc.nextLine();
            String[] fields = line.split(DATA_SEPARATOR);

            dao.create(new CityInfo(fields[2], Double.parseDouble(fields[0]), Double.parseDouble(fields[1])));
        }

        in.close();
        sc.close();
    }

    private void loadCountries(ConnectionSource connectionSource) throws IOException, SQLException {
        AssetManager assets = context.getAssets();
        InputStream in = assets.open("countries.txt");
        Scanner sc = new Scanner(in);
        Dao<CountryInfo, String> dao = DaoManager.createDao(connectionSource, CountryInfo.class);

        while(sc.hasNextLine()){
            String line = sc.nextLine();
            String[] fields = line.split(DATA_SEPARATOR);

            dao.create(new CountryInfo(fields[4], fields[2]));
        }

        in.close();
        sc.close();
    }

    private void loadLanguages(ConnectionSource connectionSource) throws IOException, SQLException {
        AssetManager assets = context.getAssets();
        InputStream in = assets.open("languages.txt");
        Scanner sc = new Scanner(in);
        Dao<LanguageInfo, String> dao = DaoManager.createDao(connectionSource, LanguageInfo.class);

        while(sc.hasNextLine()){
            String line = sc.nextLine();
            String[] fields = line.split(DATA_SEPARATOR);

            dao.create(new LanguageInfo(fields[0], fields[1]));
        }

        in.close();
        sc.close();
    }

    private void loadColors(ConnectionSource connectionSource) throws IOException, SQLException {
        AssetManager assets = context.getAssets();
        InputStream in = assets.open("colors.txt");
        Scanner sc = new Scanner(in);
        Dao<ColorInfo, String> dao = DaoManager.createDao(connectionSource, ColorInfo.class);

        while(sc.hasNextLine()){
            String line = sc.nextLine();
            String[] fields = line.split(DATA_SEPARATOR);

            dao.create(new ColorInfo(fields[0], fields[1]));
        }

        in.close();
        sc.close();
    }

    private void loadVerbs(ConnectionSource connectionSource) throws IOException, SQLException {
        AssetManager assets = context.getAssets();
        InputStream in = assets.open("verbs.txt");
        Scanner sc = new Scanner(in, SPECIAL_ENCODING);
        Dao<Verb, String> dao = DaoManager.createDao(connectionSource, Verb.class);

        while(sc.hasNextLine()){
            String line = sc.nextLine();
            String[] fields = line.split(DATA_SEPARATOR);

            dao.create(new Verb(fields[0], Action.VerbAction.valueOf(fields[1]), (Integer.parseInt(fields[3]) != 0)));
        }

        in.close();
        sc.close();
    }

    private void loadConjugations(ConnectionSource connectionSource) throws IOException, SQLException {
        AssetManager assets = context.getAssets();
        InputStream in = assets.open("conjugations.txt");
        Scanner sc = new Scanner(in, SPECIAL_ENCODING);
        Dao<VerbConjugation, String> dao = DaoManager.createDao(connectionSource, VerbConjugation.class);
        Dao<Verb, String> verbDao = DaoManager.createDao(connectionSource, Verb.class);

        while(sc.hasNextLine()){
            String line = sc.nextLine();
            String[] fields = line.split(DATA_SEPARATOR);
            Verb.Pronoun pronoun = null;

            try{
                pronoun = Verb.Pronoun.values()[Integer.parseInt(fields[4])];
            }catch(Exception e){

            }

            dao.create(new VerbConjugation(Verb.ConjugationTense.valueOf(fields[3]),
                    Verb.Form.valueOf(fields[2]),
                    pronoun,
                    fields[1],
                    verbDao.queryForId(fields[0])
            ));
        }

        in.close();
        sc.close();
    }

    private void loadPlacePrepositions(ConnectionSource connectionSource) throws IOException, SQLException {
        AssetManager assets = context.getAssets();
        InputStream in = assets.open("place_prepositions.txt");
        Scanner sc = new Scanner(in);
        Dao<PlacePreposition, String> dao = DaoManager.createDao(connectionSource, PlacePreposition.class);

        while(sc.hasNextLine()){
            String line = sc.nextLine();
            String[] fields = line.split(DATA_SEPARATOR);
            String[] types = fields[2].split(",");
            Set<PlaceAdverbial.PlaceType> prepTypes = new HashSet<>();

            for(String ty: types){
                prepTypes.add(PlaceAdverbial.PlaceType.valueOf(ty));
            }

            dao.create(new PlacePreposition(fields[0], PlaceAdverbial.PlaceContext.valueOf(fields[1]), prepTypes));
        }

        in.close();
        sc.close();
    }

    private void loadTimePrepositions(ConnectionSource connectionSource) throws IOException, SQLException {
        AssetManager assets = context.getAssets();
        InputStream in = assets.open("time_prepositions.txt");
        Scanner sc = new Scanner(in);
        Dao<TimePreposition, String> dao = DaoManager.createDao(connectionSource, TimePreposition.class);

        while(sc.hasNextLine()){
            String line = sc.nextLine();
            String[] fields = line.split(DATA_SEPARATOR);
            String[] types = fields[2].split(",");
            Set<TimeAdverbial.TimeType> prepTypes = new HashSet<>();

            for(String ty: types){
                prepTypes.add(TimeAdverbial.TimeType.valueOf(ty));
            }

            dao.create(new TimePreposition(fields[0], TimeAdverbial.DateContext.valueOf(fields[1]), prepTypes));
        }

        in.close();
        sc.close();
    }

    private void loadWayPrepositions(ConnectionSource connectionSource) throws IOException, SQLException {
        AssetManager assets = context.getAssets();
        InputStream in = assets.open("way_prepositions.txt");
        Scanner sc = new Scanner(in);
        Dao<WayPreposition, String> dao = DaoManager.createDao(connectionSource, WayPreposition.class);

        while(sc.hasNextLine()){
            String line = sc.nextLine();
            String[] fields = line.split(DATA_SEPARATOR);
            String[] types = fields[2].split(",");
            Set<WayAdverbial.WayType> prepTypes = new HashSet<>();

            for(String ty: types){
                prepTypes.add(WayAdverbial.WayType.valueOf(ty));
            }

            dao.create(new WayPreposition(fields[0], WayAdverbial.WayContext.valueOf(fields[1]), prepTypes));
        }

        in.close();
        sc.close();
    }

    private void loadPurposePrepositions(ConnectionSource connectionSource) throws IOException, SQLException {
        AssetManager assets = context.getAssets();
        InputStream in = assets.open("purpose_prepositions.txt");
        Scanner sc = new Scanner(in);
        Dao<PurposePreposition, String> dao = DaoManager.createDao(connectionSource, PurposePreposition.class);

        while(sc.hasNextLine()){
            String line = sc.nextLine();
            String[] fields = line.split(DATA_SEPARATOR);
            String[] types = fields[2].split(",");
            Set<PurposeAdverbial.PurposeType> prepTypes = new HashSet<>();

            for(String ty: types){
                prepTypes.add(PurposeAdverbial.PurposeType.valueOf(ty));
            }

            dao.create(new PurposePreposition(fields[0], PurposeAdverbial.PurposeContext.valueOf(fields[1]), prepTypes));
        }

        in.close();
        sc.close();
    }

    public List<CustomProfileObject> queryProfileObjects(String profileId, String queryString){
        return (List<CustomProfileObject>)queryProfileData(CustomProfileObject.class, CustomObject.OBJECT_FIELD_NAME, queryString, profileId);
    }

    public List<CustomProfilePlace> queryProfilePlaces(String profileId, String queryString){
        return (List<CustomProfilePlace>)queryProfileData(CustomProfilePlace.class, CustomPlace.PLACE_FIELD_NAME, queryString, profileId);
    }

    public List<CustomProfilePerson> queryProfilePeople(String profileId, String queryString){
        return (List<CustomProfilePerson>)queryProfileData(CustomProfilePerson.class, CustomPerson.PERSON_FIELD_NAME, queryString, profileId);
    }

    public List<CustomProfileMode> queryProfileModes(String profileId, String queryString){
        return (List<CustomProfileMode>)queryProfileData(CustomProfileMode.class, CustomMode.MODE_FIELD_NAME, queryString, profileId);
    }

    public List<Profile> queryProfiles(){
        try {
            Dao<Profile, String> dao = DaoManager.createDao(this.getConnectionSource(), Profile.class);
            return dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<?> queryProfileData(Class<?> daoClass, String queryField, String queryValue, String profileName){
        try {
            Dao<?, ?> dataDao = DaoManager.createDao(this.getConnectionSource(), daoClass);
            QueryBuilder builder = dataDao.queryBuilder();
            Where where = builder.where();

            where.like(queryField, "%" + queryValue + "%")
                    .and()
                    .eq(Profile.PROFILE_COLUMN_NAME, profileName);
            return dataDao.query(where.prepare());
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList();
        }
    }

    public boolean addProfile(Profile p){
        try {
            Dao<Profile, String> dao = DaoManager.createDao(this.getConnectionSource(), Profile.class);
            return (dao.create(p) >= 0);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Profile getActiveProfile() throws SQLException {
        String currentProfileId = context.getSharedPreferences(Profile.PROFILE_PREF_KEY, 0)
                .getString(Profile.ACTIVE_PROFILE_KEY, Profile.DEFAULT_PROFILE_CODE);
        Dao<Profile, String> profileDao = DaoManager.createDao(this.getConnectionSource(), Profile.class);
        return profileDao.queryForId(currentProfileId);
    }

    public Profile getProfile(String profileId) throws SQLException {
        Dao<Profile, String> dao = DaoManager.createDao(this.getConnectionSource(), Profile.class);
        return dao.queryForId(profileId);
    }

    public boolean deleteProfile(String profileId){
        try {
            Dao<Profile, String> dao = DaoManager.createDao(this.getConnectionSource(), Profile.class);
            boolean result;

            for(Class<?> profileRelatedClass: PROFILE_CLASSES){
                Dao<?, ?> classDao = DaoManager.createDao(this.getConnectionSource(), profileRelatedClass);
                DeleteBuilder builder = classDao.deleteBuilder();
                builder.where().eq(Profile.PROFILE_COLUMN_NAME, profileId);
                builder.delete();
            }

            result = (dao.deleteById(profileId) >= 0);

            if(result){
                String currentProfileId = context.getSharedPreferences(Profile.PROFILE_PREF_KEY, 0)
                        .getString(Profile.ACTIVE_PROFILE_KEY, Profile.DEFAULT_PROFILE_CODE);

                if(profileId.equals(currentProfileId)){
                    // Choisir un profil actif de remplacement
                    context.getSharedPreferences(Profile.PROFILE_PREF_KEY, 0).edit()
                            .putString(Profile.ACTIVE_PROFILE_KEY, Profile.DEFAULT_PROFILE_CODE)
                            .apply();
                    EventBus.getDefault().post(new ProfileEvent(getActiveProfile()));
                }
            }

            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addCustomObject(CustomProfileObject object, String profileId){
        try {
            Dao<CustomProfileObject, String> dao = DaoManager.createDao(this.getConnectionSource(), CustomProfileObject.class);
            object.setProfile(getProfile(profileId));
            return (dao.create(object) >= 0);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addCustomPlace(CustomProfilePlace place, String profileId){
        try {
            Dao<CustomProfilePlace, String> dao = DaoManager.createDao(this.getConnectionSource(), CustomProfilePlace.class);
            place.setProfile(getProfile(profileId));
            return (dao.create(place) >= 0);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addCustomPerson(CustomProfilePerson person, String profileId){
        try {
            Dao<CustomProfilePerson, String> dao = DaoManager.createDao(this.getConnectionSource(), CustomProfilePerson.class);
            person.setProfile(getProfile(profileId));
            return (dao.create(person) >= 0);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addCustomMode(CustomProfileMode mode, String profileId){
        try {
            Dao<CustomProfileMode, String> dao = DaoManager.createDao(this.getConnectionSource(), CustomProfileMode.class);
            mode.setProfile(getProfile(profileId));
            return (dao.create(mode) >= 0);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /*private boolean addCustomData(Class<? extends ProfileRelated> dataClass, ProfileRelated profileData) {
        try {
            Dao<? extends ProfileRelated, ?> dao = DaoManager.createDao(this.getConnectionSource(), dataClass);
            profileData.setProfile(getActiveProfile());
            return (dao.create(profileData) >= 0);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }*/

    public InterpretationObject parseText(String text){
        return parser.parseText(text);
    }

    @Override
    public boolean wordStartsWith(String q){
        ConnectionSource connectionSource = this.getConnectionSource();
        boolean exists = false;

        for(int i=0; i<COMMON_CLASSES.length; i++){
            try {
                Dao<?, ?> dao = DaoManager.createDao(connectionSource, COMMON_CLASSES[i]);
                QueryBuilder builder = dao.queryBuilder();

                /*long count = builder.where()
                        .ge(COMMON_COLUMN_NAMES[i], q)
                        .and()
                        .lt(COMMON_COLUMN_NAMES[i], q + "ý").countOf();*/
                long count = builder.where()
                        .eq(COMMON_COLUMN_NAMES[i], q)
                        .or()
                        .between(COMMON_COLUMN_NAMES[i], q + " ", q + "ý").countOf();
                exists = (count > 0);

                if(exists){
                    break;
                }
            } catch (SQLException e) {

            }
        }

        if(!exists){
            String currentProfile = context.getSharedPreferences(Profile.PROFILE_PREF_KEY, 0)
                    .getString(Profile.ACTIVE_PROFILE_KEY, "default");

            for(int i=0; i<PROFILE_CLASSES.length; i++){
                try {
                    Dao<?, ?> dao = DaoManager.createDao(connectionSource, PROFILE_CLASSES[i]);
                    QueryBuilder builder = dao.queryBuilder();
                    //Where where = builder.where();

                    /*where.like(PROFILE_RELATED_COLUMN_NAMES[i], q + "%")
                            .and()
                            .eq(Profile.PROFILE_COLUMN_NAME, currentProfile);*/
                    long count = builder.where().like(PROFILE_RELATED_COLUMN_NAMES[i], q + "%")
                            .and()
                            .eq(Profile.PROFILE_COLUMN_NAME, currentProfile).countOf();

                    exists = (count > 0);
                    //exists = (dao.queryForFirst(preparedQuery) != null);

                    if(exists){
                        break;
                    }
                } catch (SQLException e) {

                }
            }
        }

        return exists;
    }

    @Override
    public Word findWord(String q) {
        return queryFirst(this, Word.class, Word.WORD_FIELD_NAME, q);
    }

    @Override
    public LanguageInfo findLanguageInfo(String q){
        return queryFirst(this, LanguageInfo.class, LanguageInfo.LANGUAGE_FIELD_NAME, q);
    }

    @Override
    public ColorInfo findColorInfo(String q){
        return queryFirst(this, ColorInfo.class, ColorInfo.COLOR_FIELD_NAME, q);
    }

    @Override
    public CityInfo findCityInfo(String q){
        return queryFirst(this, CityInfo.class, CityInfo.CITY_FIELD_NAME, q);
    }

    public CountryInfo findCountryInfo(String q){
        return queryFirst(this, CountryInfo.class, CountryInfo.COUNTRY_FIELD_NAME, q);
    }

    @Override
    public CustomObject findCustomObject(String q){
        return queryFirst(this, CustomProfileObject.class, CustomObject.OBJECT_FIELD_NAME, q);
    }

    @Override
    public CustomPlace findCustomPlace(String q){
        return queryFirst(this, CustomProfilePlace.class, CustomPlace.PLACE_FIELD_NAME, q);
    }

    @Override
    public PlacePreposition findPlacePreposition(String q) {
        return queryFirst(this, PlacePreposition.class, PlacePreposition.VALUE_FIELD_NAME, q);
    }

    @Override
    public TimePreposition findTimePreposition(String q) {
        return queryFirst(this, TimePreposition.class, TimePreposition.VALUE_FIELD_NAME, q);
    }

    @Override
    public WayPreposition findWayPreposition(String q) {
        return queryFirst(this, WayPreposition.class, WayPreposition.VALUE_FIELD_NAME, q);
    }

    @Override
    public PurposePreposition findPurposePreposition(String q) {
        return queryFirst(this, PurposePreposition.class, PurposePreposition.VALUE_FIELD_NAME, q);
    }

    @Override
    public VerbConjugation findConjugation(String q){
        return queryFirst(this, VerbConjugation.class, VerbConjugation.VALUE_FIELD_NAME, q);
    }

    private static <T> T queryFirst(OrmLiteSqliteOpenHelper helper, Class<T> objectClass, String columnName, String queryString){
        ConnectionSource connectionSource = helper.getConnectionSource();

        try {
            Dao<T, String> dao = DaoManager.createDao(connectionSource, objectClass);
            QueryBuilder builder = dao.queryBuilder();
            PreparedQuery<T> preparedQuery = builder.where().eq(columnName, queryString).prepare();
            T result = dao.queryForFirst(preparedQuery);
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}