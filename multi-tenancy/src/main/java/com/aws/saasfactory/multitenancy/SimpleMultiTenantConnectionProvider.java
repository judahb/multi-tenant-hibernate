package com.aws.saasfactory.multitenancy;

import java.util.Properties;

import org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl;
import org.hibernate.engine.jdbc.connections.spi.AbstractMultiTenantConnectionProvider;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;

public class SimpleMultiTenantConnectionProvider extends AbstractMultiTenantConnectionProvider {


    public static final String TENANT_ID_1 = "Tenant1";
    public static final String TENANT_ID_2 = "Tenant2";

    public static final String DB_NAME = "Test";

    public static final String DB_NAME_1 = TENANT_ID_1 + DB_NAME;
    public static final String DB_NAME_2 = TENANT_ID_2 + DB_NAME;

    private static final String DRIVER = "org.h2.Driver";
//    private static final String URL = "jdbc:h2:mem:%s;DB_CLOSE_DELAY=-1;MVCC=TRUE";
    private static final String URL = "jdbc:h2:file:~/%s;DB_CLOSE_DELAY=-1;MVCC=TRUE";
    private static final String USER = "sa";
    private static final String PASS = "";

    private final ConnectionProvider foo1ConnectionProvider = buildConnectionProvider( DB_NAME_1 );
    private final ConnectionProvider foo2ConnectionProvider = buildConnectionProvider( DB_NAME_2 );

    @Override
    protected ConnectionProvider getAnyConnectionProvider() {
        return foo1ConnectionProvider;
    }

    @Override
    protected ConnectionProvider selectConnectionProvider(String tenantIdentifier) {
        if ( TENANT_ID_1.equals( tenantIdentifier ) ) {
            return foo1ConnectionProvider;
        }
        else if ( TENANT_ID_2.equals( tenantIdentifier ) ) {
            return foo2ConnectionProvider;
        }
        throw new IllegalArgumentException( "Unknown tenant identifier" );
    }

    private ConnectionProvider buildConnectionProvider(String dbName) {
        Properties props = new Properties( null );
        props.put( "hibernate.connection.driver_class", DRIVER );
        // Inject dbName into connection url string.
        props.put( "hibernate.connection.url", String.format( URL, dbName ) );
        props.put( "hibernate.connection.username", USER );
        props.put( "hibernate.connection.password", PASS );

        // Note that DriverManagerConnectionProviderImpl is an internal class.  However, rather than creating
        // a ConnectionProvider, I'm using it for simplicity's sake.
        // DriverManagerConnectionProviderImpl obtains a Connection through the JDBC Driver#connect
        DriverManagerConnectionProviderImpl connectionProvider = new DriverManagerConnectionProviderImpl();
        connectionProvider.configure( props );
        return connectionProvider;
    }


}
