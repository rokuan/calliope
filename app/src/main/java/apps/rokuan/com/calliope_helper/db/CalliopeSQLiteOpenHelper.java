package apps.rokuan.com.calliope_helper.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.StatementBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.CompiledStatement;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.support.DatabaseResults;
import com.j256.ormlite.table.TableUtils;
import com.rokuan.calliopecore.parser.Parser;
import com.rokuan.calliopecore.parser.SpeechParser;
import com.rokuan.calliopecore.parser.WordBuffer;
import com.rokuan.calliopecore.sentence.CityInfo;
import com.rokuan.calliopecore.sentence.CountryInfo;
import com.rokuan.calliopecore.sentence.CustomObject;
import com.rokuan.calliopecore.sentence.CustomPlace;
import com.rokuan.calliopecore.sentence.LanguageInfo;
import com.rokuan.calliopecore.sentence.PlacePreposition;
import com.rokuan.calliopecore.sentence.TimePreposition;
import com.rokuan.calliopecore.sentence.Verb;
import com.rokuan.calliopecore.sentence.VerbConjugation;
import com.rokuan.calliopecore.sentence.Word;
import com.rokuan.calliopecore.sentence.structure.InterpretationObject;
import com.rokuan.calliopecore.sentence.structure.data.place.PlaceObject;
import com.rokuan.calliopecore.sentence.structure.data.time.TimeObject;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by LEBEAU Christophe on 18/07/15.
 */
public class CalliopeSQLiteOpenHelper extends OrmLiteSqliteOpenHelper implements SpeechParser {
    private static final String DB_NAME = "calliope_helper";
    private static final int DB_VERSION = 1;

    public CalliopeSQLiteOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Profile.class);
            TableUtils.createTable(connectionSource, CityInfo.class);
            TableUtils.createTable(connectionSource, CountryInfo.class);
            TableUtils.createTable(connectionSource, LanguageInfo.class);
            // TODO: tables des couleurs (et prenoms ?)
            TableUtils.createTable(connectionSource, Verb.class);
            TableUtils.createTable(connectionSource, VerbConjugation.class);
            TableUtils.createTable(connectionSource, CustomObject.class);
            TableUtils.createTable(connectionSource, CustomPlace.class);
            TableUtils.createTable(connectionSource, PlacePreposition.class);
            TableUtils.createTable(connectionSource, TimePreposition.class);

            Profile defaultProfile = new Profile("_default", "Defaut");
            Dao<Profile, String> profileDao = DaoManager.createDao(connectionSource, Profile.class);

            profileDao.create(defaultProfile);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {

    }

    @Override
    public WordBuffer lexSpeech(String s) {
        WordBuffer buffer = new WordBuffer();
        String[] words = s.split(" ");

        for(int i=0; i<words.length; i++){
            Word tmpWord = null, currentWord = null;
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
                    } else {
                        buffer.add(leftWord);
                        wordBuilder = new StringBuilder(rightPart);
                    }
                } else {

                }
            }

            shouldContinue = false;


            /*currentWord = findWord(wordBuilder.toString());

            if(currentWord == null){

            }

            do {
                tmpWord = currentWord;
                currentWord = findWord(words[i]);

            } while(tmpWord != null);*/
        }

        return buffer;
    }

    @Override
    public InterpretationObject parseSpeech(WordBuffer words) {
        return new Parser().parseInterpretationObject(words);
    }

    public Word findWord(String q){
        Set<Word.WordType> types = new HashSet<>();
        Word result = queryFirst(this, Word.class, Word.WORD_FIELD_NAME, q);

        LanguageInfo language = findLanguageInfo(q);
        CityInfo city = findCityInfo(q);
        CountryInfo country = findCountryInfo(q);
        CustomObject object = findCustomObject(q);
        CustomPlace place = findCustomPlace(q);
        PlacePreposition placePreposition = null;
        TimePreposition timePreposition = null;

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

            if(placePreposition != null){
                result.setPlacePreposition(placePreposition.getPlacePreposition());
            }

            if(timePreposition != null){
                result.setDatePreposition(timePreposition.getTimePreposition());
            }
        }

        return result;
    }

    private boolean wordStartsWith(String q){
        ConnectionSource connectionSource = this.getConnectionSource();
        Class<?>[] classes = {
                Word.class,
                LanguageInfo.class,
                CityInfo.class,
                CountryInfo.class,
                CustomObject.class,
                CustomPlace.class
        };
        String[] columnNames = {
                Word.WORD_FIELD_NAME,
                LanguageInfo.LANGUAGE_FIELD_NAME,
                CityInfo.CITY_FIELD_NAME,
                CountryInfo.COUNTRY_FIELD_NAME,
                CustomObject.OBJECT_FIELD_NAME,
                CustomPlace.PLACE_FIELD_NAME
        };
        boolean exists = false;

        for(int i=0; i<classes.length; i++){
            try {
                Dao<?, ?> dao = DaoManager.createDao(connectionSource, classes[i]);
                QueryBuilder builder = dao.queryBuilder();
                //PreparedQuery<?> preparedQuery = builder.where().like(columnNames[i], q + "%").ex;
                exists = (builder.where().like(columnNames[i], q + "%").countOf() > 0);

                if(exists){
                    break;
                }
            } catch (SQLException e) {

            }
        }

        try {
            connectionSource.close();
        } catch (SQLException e) {

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
        return queryFirst(this, CustomObject.class, CustomObject.OBJECT_FIELD_NAME, q);
    }

    public CustomPlace findCustomPlace(String q){
        return queryFirst(this, CustomPlace.class, CustomPlace.PLACE_FIELD_NAME, q);
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
        } finally {
            try {
                connectionSource.close();
            } catch (SQLException e) {

            }
        }
    }
}
