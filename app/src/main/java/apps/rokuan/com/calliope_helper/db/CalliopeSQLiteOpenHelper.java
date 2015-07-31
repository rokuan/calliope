package apps.rokuan.com.calliope_helper.db;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.rokuan.calliopecore.parser.Parser;
import com.rokuan.calliopecore.parser.SpeechParser;
import com.rokuan.calliopecore.parser.WordBuffer;
import com.rokuan.calliopecore.sentence.Action;
import com.rokuan.calliopecore.sentence.CityInfo;
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
import com.rokuan.calliopecore.sentence.structure.way.WayAdverbial;

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

import de.greenrobot.event.EventBus;

/**
 * Created by LEBEAU Christophe on 18/07/15.
 */
public class CalliopeSQLiteOpenHelper extends OrmLiteSqliteOpenHelper implements SpeechParser {
    public static final String DATA_SEPARATOR = ";";
    public static final String SPECIAL_ENCODING = "ISO-8859-1";

    private static final Class<?>[] COMMON_CLASSES = {
            Word.class,
            LanguageInfo.class,
            CityInfo.class,
            CountryInfo.class,
            VerbConjugation.class,
            PlacePreposition.class,
            TimePreposition.class
    };
    private static final String[] COMMON_COLUMN_NAMES = {
            Word.WORD_FIELD_NAME,
            LanguageInfo.LANGUAGE_FIELD_NAME,
            CityInfo.CITY_FIELD_NAME,
            CountryInfo.COUNTRY_FIELD_NAME,
            VerbConjugation.VALUE_FIELD_NAME,
            PlacePreposition.VALUE_FIELD_NAME,
            TimePreposition.VALUE_FIELD_NAME
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

    public CalliopeSQLiteOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Profile.class);
            TableUtils.createTable(connectionSource, Word.class);
            TableUtils.createTable(connectionSource, CityInfo.class);
            TableUtils.createTable(connectionSource, CountryInfo.class);
            TableUtils.createTable(connectionSource, LanguageInfo.class);
            // TODO: tables des couleurs (et prenoms ?)
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

            defaultBus.post(new DatabaseEvent("Profiles"));
            loadProfiles(connectionSource);
            defaultBus.post(new DatabaseEvent("Mots"));
            loadWords(connectionSource);
            defaultBus.post(new DatabaseEvent("Villes"));
            loadCities(connectionSource);
            defaultBus.post(new DatabaseEvent("Pays"));
            loadCountries(connectionSource);
            defaultBus.post(new DatabaseEvent("Langues"));
            loadLanguages(connectionSource);
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

    private void loadVerbs(ConnectionSource connectionSource) throws IOException, SQLException {
        AssetManager assets = context.getAssets();
        InputStream in = assets.open("verbs.txt");
        Scanner sc = new Scanner(in, SPECIAL_ENCODING);
        Dao<Verb, String> dao = DaoManager.createDao(connectionSource, Verb.class);

        while(sc.hasNextLine()){
            String line = sc.nextLine();
            String[] fields = line.split(DATA_SEPARATOR);

            dao.create(new Verb(fields[0], Action.VerbAction.valueOf(fields[1]), (Integer.parseInt(fields[3]) == 0)));
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

            dao.create(new PlacePreposition(fields[0], PlaceAdverbial.PlaceContext.valueOf(fields[1])));
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

            dao.create(new TimePreposition(fields[0], TimeAdverbial.DateContext.valueOf(fields[1])));
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

            dao.create(new WayPreposition(fields[0], WayAdverbial.WayContext.valueOf(fields[1])));
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

            dao.create(new PurposePreposition(fields[0], PurposeAdverbial.PurposeContext.valueOf(fields[1])));
        }

        in.close();
        sc.close();
    }

    public List<CustomProfileObject> queryProfileObjects(String queryString){
        return (List<CustomProfileObject>)queryProfileData(CustomProfileObject.class, CustomObject.OBJECT_FIELD_NAME, queryString);
    }

    public List<CustomProfilePlace> queryProfilePlaces(String queryString){
        return (List<CustomProfilePlace>)queryProfileData(CustomProfilePlace.class, CustomPlace.PLACE_FIELD_NAME, queryString);
    }

    public List<CustomProfilePerson> queryProfilePeople(String queryString){
        return (List<CustomProfilePerson>)queryProfileData(CustomProfilePerson.class, CustomPerson.PERSON_FIELD_NAME, queryString);
    }

    public List<CustomProfileMode> queryProfileModes(String queryString){
        return (List<CustomProfileMode>)queryProfileData(CustomProfileMode.class, CustomMode.MODE_FIELD_NAME, queryString);
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

    private List<?> queryProfileData(Class<?> daoClass, String queryField, String queryValue){
        try {
            Dao<?, ?> dataDao = DaoManager.createDao(this.getConnectionSource(), daoClass);
            String currentProfile = context.getSharedPreferences(Profile.PROFILE_PREF_KEY, 0)
                    .getString(Profile.ACTIVE_PROFILE_KEY, Profile.DEFAULT_PROFILE_CODE);
            QueryBuilder builder = dataDao.queryBuilder();
            Where where = builder.where();

            where.like(queryField, "%" + queryValue + "%")
                    .and()
                    .eq(Profile.PROFILE_COLUMN_NAME, currentProfile);
            return dataDao.query(where.prepare());
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList();
        }
    }

    public Profile getActiveProfile() throws SQLException {
        String currentProfileId = context.getSharedPreferences(Profile.PROFILE_PREF_KEY, 0)
                .getString(Profile.ACTIVE_PROFILE_KEY, Profile.DEFAULT_PROFILE_CODE);
        Dao<Profile, String> profileDao = DaoManager.createDao(this.getConnectionSource(), Profile.class);
        return profileDao.queryForId(currentProfileId);
    }

    public boolean addCustomObject(CustomProfileObject object){
        try {
            Dao<CustomProfileObject, String> dao = DaoManager.createDao(this.getConnectionSource(), CustomProfileObject.class);
            object.setProfile(getActiveProfile());
            return (dao.create(object) >= 0);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addCustomPlace(CustomProfilePlace place){
        try {
            Dao<CustomProfilePlace, String> dao = DaoManager.createDao(this.getConnectionSource(), CustomProfilePlace.class);
            place.setProfile(getActiveProfile());
            return (dao.create(place) >= 0);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addCustomPerson(CustomProfilePerson person){
        try {
            Dao<CustomProfilePerson, String> dao = DaoManager.createDao(this.getConnectionSource(), CustomProfilePerson.class);
            person.setProfile(getActiveProfile());
            return (dao.create(person) >= 0);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addCustomMode(CustomProfileMode mode){
        try {
            Dao<CustomProfileMode, String> dao = DaoManager.createDao(this.getConnectionSource(), CustomProfileMode.class);
            mode.setProfile(getActiveProfile());
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

    @Override
    public WordBuffer lexSpeech(String s) {
        WordBuffer buffer = new WordBuffer();
        String[] words = s.split(" ");

        for(int i=0; i<words.length; i++){
            Word currentWord = null;
            StringBuilder wordBuilder = new StringBuilder(words[i]);
            boolean shouldContinue = wordStartsWith(wordBuilder.toString());

            if(!shouldContinue){
                int charIndex = words[i].indexOf('\'');

                if(charIndex != -1) {
                    String leftPart = words[i].substring(0, charIndex);
                    String rightPart = words[i].substring(charIndex + 1, words[i].length());

                    Word leftWord = findWord(leftPart);

                    // Mot d'une autre langue
                    if(leftWord == null){
                        buffer.add(new Word(words[i], Word.WordType.OTHER));
                        continue;
                    } else {
                        buffer.add(leftWord);
                        wordBuilder = new StringBuilder(rightPart);
                        shouldContinue = true;
                    }
                }

                String tmpPart = wordBuilder.toString();
                Word tmpWord = findWord(tmpPart);

                if(tmpWord == null) {
                    // TODO: faire le split sur le tiret
                    charIndex = tmpPart.indexOf('-');

                    if (charIndex != -1) {
                        String leftPart = tmpPart.substring(0, charIndex);
                        String rightPart = tmpPart.substring(charIndex + 1, words[i].length());

                        Word leftWord = findWord(leftPart);

                        // Mot d'une autre langue
                        if(leftWord == null){
                            buffer.add(new Word(words[i], Word.WordType.OTHER));
                            continue;
                        } else {
                            buffer.add(leftWord);
                            wordBuilder = new StringBuilder(rightPart);
                            shouldContinue = true;
                        }
                    }
                } else {
                    shouldContinue = true;
                }
            }

            while(shouldContinue && i < words.length){
                currentWord = findWord(wordBuilder.toString());

                i++;

                if(i < words.length) {
                    wordBuilder.append(' ');
                    wordBuilder.append(words[i]);
                    shouldContinue = wordStartsWith(wordBuilder.toString());

                    if(!shouldContinue){
                        i--;
                    }
                }
            }

            buffer.add(currentWord);
        }

        Log.i("CalliopeSQL", "wordBuffer=" + buffer);

        return buffer;
    }

    @Override
    public InterpretationObject parseSpeech(WordBuffer words) {
        return new Parser().parseInterpretationObject(words);
    }

    public InterpretationObject parseText(String text){
        return this.parseSpeech(this.lexSpeech(text));
    }

    public Word findWord(String q){
        // TODO: PROPER_NAME, NUMBER
        if(q.matches(DateConverter.FULL_TIME_REGEX) || q.matches(DateConverter.HOUR_ONLY_REGEX)){
            return new Word(q, Word.WordType.TIME);
        }

        /*if(Character.isDigit(w.charAt(0))){
            return new Word(Word.WordType.NUMBER, w);
        }*/
        /*if(Character.isDigit(q.charAt(0))) {
            try {
                return new Word(q, Word.WordType.NUMBER);
            } catch (Exception e) {
                Matcher matcher = Pattern.compile("[0-9]+e").matcher(w);

                if (matcher.find()) {
                    String matchingValue = matcher.group(0);
                    long longValue = Long.parseLong(matchingValue.substring(0, matchingValue.length() - 1));
                    return new Word(String.valueOf(longValue), Word.WordType.NUMERICAL_POSITION);
                }
            }
        }*/
        try{
            Integer.parseInt(q);
            return new Word(q, Word.WordType.NUMBER);
        }catch(Exception e){
            // TODO: les positions de la forme [0-9]eme
            Matcher matcher = Pattern.compile("[0-9]+e").matcher(q);

            if (matcher.find()) {
                String matchingValue = matcher.group(0);
                long longValue = Long.parseLong(matchingValue.substring(0, matchingValue.length() - 1));
                return new Word(String.valueOf(longValue), Word.WordType.NUMERICAL_POSITION);
            }
        }

        Set<Word.WordType> types = new HashSet<>();
        Word result = queryFirst(this, Word.class, Word.WORD_FIELD_NAME, q);

        LanguageInfo language = findLanguageInfo(q);
        CityInfo city = findCityInfo(q);
        CountryInfo country = findCountryInfo(q);
        CustomObject object = findCustomObject(q);
        CustomPlace place = findCustomPlace(q);
        PlacePreposition placePreposition = findPlacePreposition(q);
        TimePreposition timePreposition = findTimePreposition(q);
        WayPreposition wayPreposition = findWayPreposition(q);
        PurposePreposition purposePreposition = findPurposePreposition(q);
        VerbConjugation conjugation = findConjugation(q);

        if(Character.isUpperCase(q.charAt(0))){
            if(city == null && country == null) {
                types.add(Word.WordType.PROPER_NAME);
            }
        } else {
            if(CountConverter.isAPosition(q)){
                types.add(Word.WordType.NUMERICAL_POSITION);
            }
        }

        if(language != null){
            types.add(Word.WordType.LANGUAGE);
        }

        if(city != null){
            types.add(Word.WordType.CITY);
        }

        if(country != null){
            types.add(Word.WordType.COUNTRY);
        }

        if(object != null){
            types.add(Word.WordType.OBJECT);
        }

        if(place != null){
            types.add(Word.WordType.ADDITIONAL_PLACE);
        }

        if(placePreposition != null){
            types.add(Word.WordType.PLACE_PREPOSITION);
        }

        if(timePreposition != null){
            types.add(Word.WordType.TIME_PREPOSITION);
        }

        if(wayPreposition != null){
            types.add(Word.WordType.WAY_PREPOSITION);
        }

        if(purposePreposition != null){
            types.add(Word.WordType.PURPOSE_PREPOSITION);
        }

        if(conjugation != null){
            types.add(Word.WordType.VERB);

            if(conjugation.getVerb().isAuxiliary()){
                types.add(Word.WordType.AUXILIARY);
            }
        }

        if (result == null){
            if(!types.isEmpty()) {
                result = new Word(q, types);
            }
        } else {
            for(Word.WordType t: types) {
                result.addType(t);
            }
        }

        if(result != null) {
            result.setLanguageInfo(language);
            result.setCityInfo(city);
            result.setCountryInfo(country);
            result.setCustomObject(object);
            result.setCustomPlace(place);
            result.setVerbInfo(conjugation);

            if(placePreposition != null){
                result.setPlacePreposition(placePreposition.getPlacePreposition());
            }

            if(timePreposition != null){
                result.setDatePreposition(timePreposition.getTimePreposition());
            }

            if(wayPreposition != null){
                result.setWayPreposition(wayPreposition.getWayPreposition());
            }

            if(purposePreposition != null){
                result.setPurposePreposition(purposePreposition.getPurposePreposition());
            }
        }

        return result;
    }

    private boolean wordStartsWith(String q){
        ConnectionSource connectionSource = this.getConnectionSource();
        boolean exists = false;

        for(int i=0; i<COMMON_CLASSES.length; i++){
            try {
                Dao<?, ?> dao = DaoManager.createDao(connectionSource, COMMON_CLASSES[i]);
                QueryBuilder builder = dao.queryBuilder();
                long count = builder.where().like(COMMON_COLUMN_NAMES[i], q + "%").countOf();
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
                    Where where = builder.where();

                    where.like(PROFILE_RELATED_COLUMN_NAMES[i], q + "%")
                            .and()
                            .eq(Profile.PROFILE_COLUMN_NAME, currentProfile);



                    exists = (where.countOf() > 0);

                    if(exists){
                        break;
                    }
                } catch (SQLException e) {

                }
            }
        }

        return exists;
    }

    public LanguageInfo findLanguageInfo(String q){
        return queryFirst(this, LanguageInfo.class, LanguageInfo.LANGUAGE_FIELD_NAME, q);
    }

    public CityInfo findCityInfo(String q){
        return queryFirst(this, CityInfo.class, CityInfo.CITY_FIELD_NAME, q);
    }

    public CountryInfo findCountryInfo(String q){
        return queryFirst(this, CountryInfo.class, CountryInfo.COUNTRY_FIELD_NAME, q);
    }

    public CustomObject findCustomObject(String q){
        return queryFirst(this, CustomProfileObject.class, CustomObject.OBJECT_FIELD_NAME, q);
    }

    public CustomPlace findCustomPlace(String q){
        return queryFirst(this, CustomProfilePlace.class, CustomPlace.PLACE_FIELD_NAME, q);
    }

    public PlacePreposition findPlacePreposition(String q) {
        return queryFirst(this, PlacePreposition.class, PlacePreposition.VALUE_FIELD_NAME, q);
    }

    public TimePreposition findTimePreposition(String q) {
        return queryFirst(this, TimePreposition.class, TimePreposition.VALUE_FIELD_NAME, q);
    }

    public WayPreposition findWayPreposition(String q) {
        return queryFirst(this, WayPreposition.class, WayPreposition.VALUE_FIELD_NAME, q);
    }

    public PurposePreposition findPurposePreposition(String q) {
        return queryFirst(this, PurposePreposition.class, PurposePreposition.VALUE_FIELD_NAME, q);
    }

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
