package ai.subut.kurjun.db.file;


import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import ai.subut.kurjun.metadata.common.utils.MetadataUtils;


/**
 * File based db. This db stores records in a key value pairs.
 */
public class FileDb implements Closeable
{
    private static final Logger LOGGER = LoggerFactory.getLogger( FileDb.class );

    private final Map<String, Map<Object, ?>> mapOfMap = new ConcurrentHashMap<>( 10 );

    Gson gson = new Gson();

    // to make osgi or other frameworks compatible
    private ClassLoader classLoader;

    //default value for cache dir
    private String ROOT_DIR = "/tmp/";


    public FileDb( String dbFile ) throws IOException
    {
        //set root dir for cache metadata
        this.ROOT_DIR = dbFile;

        init();

        loadMapOfMaps();
    }


    public FileDb( String dbFile, ClassLoader classLoader ) throws IOException
    {
        //set root dir for cache metadata
        this.ROOT_DIR = dbFile;
        this.classLoader = classLoader;

        init();

        loadMapOfMaps();
    }


    private void init()
    {
        createDir( ROOT_DIR );
    }


    public synchronized boolean contains( final String mapName, final Object key )
    {
        if ( !mapOfMap.containsKey( mapName ) )
        {
            return false;
        }
        return mapOfMap.get( mapName ).containsKey( key );
    }


    public synchronized <T> T get( String mapName, Object key, Class<T> clazz )
    {
        if ( mapOfMap.containsKey( mapName ) )
        {
            return ( T ) mapOfMap.get( mapName ).get( key );
        }

        return null;
    }


    public synchronized <K, V> Map<K, V> get( String mapName )
    {
        Map<K, V> copyMe = ( Map<K, V> ) mapOfMap.get( mapName );

        if ( copyMe == null )
        {
            Map<K, V> map = new HashMap<>();
            mapOfMap.put( mapName, ( Map<Object, ?> ) map );
            return map;
        }

        Map<K, V> result = new HashMap<>( copyMe.size() );
        result.putAll( copyMe );
        return result;
    }


    public synchronized <T> T put( String mapName, Object key, T value )
    {
        Map<Object, T> map = ( Map<Object, T> ) mapOfMap.get( mapName );

        if ( map != null )
        {
            persist( mapName, key, value );
            return map.put( key, value );
        }

        map = new HashMap<>();

        mapOfMap.put( mapName, map );
        //write to fs

        persist( mapName, key, value );

        return map.put( key, value );
    }


    public synchronized <T> T remove( String mapName, Object key )
    {
        Map<Object, T> map = ( Map<Object, T> ) mapOfMap.get( mapName );

        if ( map != null )
        {
            try
            {
                removeJsonFile( mapName, key );
            }
            catch ( IOException e )
            {
                LOGGER.error( "****** Error in FileDb.remove:", e );
            }
            return map.remove( key );
        }

        return null;
    }


    private synchronized void removeJsonFile( String mapName, Object key ) throws IOException
    {
        String targetDir = ROOT_DIR + "/" + mapName + "/" + String.valueOf( key ) + ".json";

        Path path = new File( targetDir ).toPath();

        Files.deleteIfExists( path );
    }


    @Override
    public synchronized void close() throws IOException
    {

    }


    //persist replacing previous file
    private synchronized void persist( String mapName, Object key, Object value )
    {
        String targetDir = ROOT_DIR + "/" + mapName + "/";
        //create dir if does not exist
        createDir( targetDir );

        try
        {
            JsonWrapper obj = new JsonWrapper( value.getClass().getName(), MetadataUtils.JSON.toJson( value ) );


            if ( new File( targetDir + key + ".json" ).exists() )
            {
                boolean success = new File( targetDir + key + ".json" ).delete();
            }

            Path tmpPath = Files.createFile( new File( targetDir + key + ".json" ).toPath() );

            try ( FileOutputStream fileOutputStream = new FileOutputStream( tmpPath.toFile() );
                  ObjectOutputStream objectOutputStream = new ObjectOutputStream( fileOutputStream ) )
            {
                objectOutputStream.writeObject( obj );
                objectOutputStream.flush();
            }
        }
        catch ( IOException e )
        {
            LOGGER.error( "****** Error in FileDb.persist:", e );
        }
    }


    /**
     * Flush flashes content of the Map of Maps to FS
     */


    private void flush()
    {
    }


    /**
     * Creates dir if does not exist
     *
     * @param mapName - dir name
     *
     * @return true if dir exists or created successfully, false otherwise
     */
    private synchronized boolean createDir( String mapName )
    {
        return new File( mapName ).exists() || new File( mapName ).mkdirs();
    }


    private synchronized void loadMapOfMaps() throws IOException
    {
        File fileDirectory = new File( this.ROOT_DIR );

        File[] files = fileDirectory.listFiles();

        if ( files != null )
        {
            //for files in root dir
            for ( File file : files )
            {
                //if it is a dir, search of .json files
                if ( file.isDirectory() )
                {
                    loadFromDir( file );
                }
                else
                {
                    try
                    {
                        loadJsonFile( file, new HashMap<>() );
                    }
                    catch ( Exception e )
                    {
                        LOGGER.error( "****** Error in FileDb.loadMapOfMaps:", e );
                    }
                }
            }
        }
    }


    private synchronized void loadFromDir( File file ) throws FileNotFoundException
    {
        //get all json files in that dir
        File jsonFiles[] = file.listFiles( filenameFilter() );

        Map map = new HashMap<>();
        //for each json file convert back to type

        for ( File jsonFile : jsonFiles )
        {
            try
            {
                loadJsonFile( jsonFile, map );
            }
            catch ( Exception e )
            {
                LOGGER.error( "****** Error in FileDb.loadFromDir:", e );
            }
        }

        mapOfMap.put( file.getName(), map );
    }


    private synchronized Map loadJsonFile( File jsonFile, Map map ) throws Exception
    {
        if ( !jsonFile.getName().endsWith( ".json" ) )
        {
            throw new Exception( "Cannot load file: " + jsonFile.getName() );
        }

        try ( FileInputStream fileInputStream = new FileInputStream( jsonFile );
              ObjectInputStream objectInputStream = new ObjectInputStream( fileInputStream ) )
        {
            JsonWrapper jsonWrapper = ( JsonWrapper ) objectInputStream.readObject();

            Class clazz = null;
            if ( classLoader != null )
            {
                clazz = classLoader.loadClass( jsonWrapper.getClassType() );
            }
            else
            {
                clazz = Class.forName( jsonWrapper.getClassType() );
            }

            Object object = gson.fromJson( jsonWrapper.getJsonObject(), clazz );
            //filename.json -> filename
            String filename = jsonFile.getName();
            String[] parsedName = filename.split( "\\." );
            if ( parsedName.length == 3 )
            {
                map.put( parsedName[1], object );
            }
            else if ( parsedName.length == 2 )
            {
                map.put( parsedName[0], object );
            }
            else
            {
                //omit file extension `.json`
                map.put( filename.substring( 0, filename.length() - 5 ), object );
            }
        }
        catch ( ClassNotFoundException | IOException e )
        {
            LOGGER.error( "****** Error in FileDb.loadJsonFile:", e );
        }
        return map;
    }


    private FilenameFilter filenameFilter()
    {
        FilenameFilter filter = ( dir, name ) -> name.endsWith( ".json" );

        return filter;
    }
}
