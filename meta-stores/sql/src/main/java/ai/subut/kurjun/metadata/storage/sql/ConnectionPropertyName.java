package ai.subut.kurjun.metadata.storage.sql;


import java.util.HashSet;
import java.util.Properties;
import java.util.Set;


/**
 * Property names to be used in {@link Properties} instance given to {@link SqlDbPackageMetadataStore} constructor. All
 * data source related properties must be correctly set. If non-standard port has to be used it can be specified with
 * server name property in a typical notation like {@code hostname:1234}.
 *
 */
public class ConnectionPropertyName
{

    /**
     * Class name of the data source to be used to connect to DB. Corresponding dependency should be included in
     * {@code pom.ml} file. By default, MariaDB/MySQL dependency is included.
     */
    public static final String DATASOURCE_CLASS_NAME = "dataSourceClassName";

    /**
     * Server name to connect to. It may include port number.
     */
    public static final String DATASOURCE_SERVER_NAME = "dataSource.serverName";

    public static final String DATASOURCE_DATABASE_NAME = "dataSource.databaseName";
    public static final String DATASOURCE_USER = "dataSource.user";
    public static final String DATASOURCE_PASS = "dataSource.password";

    // optional properties
    public static final String OPT_CONNECTION_TIMEOUT = "connectionTimeout";
    public static final String OPT_IDLE_TIMEOUT = "idleTimeout";
    public static final String OPT_MINIMUM_IDLE = "minimumIdle";
    public static final String OPT_MAX_POOL_SIZE = "maximumPoolSize ";


    private ConnectionPropertyName()
    {
        // not to be constructed
    }


    /**
     * Gets a set of all property names defined in this class.
     *
     * @return set of property names
     */
    public static Set<String> getPropertyNames()
    {
        Set<String> set = new HashSet<>();
        set.add( DATASOURCE_CLASS_NAME );
        set.add( DATASOURCE_SERVER_NAME );
        set.add( DATASOURCE_DATABASE_NAME );
        set.add( DATASOURCE_USER );
        set.add( DATASOURCE_PASS );
        set.add( OPT_CONNECTION_TIMEOUT );
        set.add( OPT_IDLE_TIMEOUT );
        set.add( OPT_MINIMUM_IDLE );
        set.add( OPT_MAX_POOL_SIZE );
        return set;
    }

}

