package ai.subut.kurjun.web.service.impl;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ai.subut.kurjun.model.identity.UserSession;
import com.google.inject.Inject;

import ai.subut.kurjun.common.service.KurjunProperties;
import ai.subut.kurjun.metadata.storage.file.DbFilePackageMetadataStoreModule;
import ai.subut.kurjun.web.service.RepositoryService;


public class RepositoryServiceImpl implements RepositoryService
{
    private UserSession userSession;

    @Inject KurjunProperties kurjunProperties;


    @Override
    public synchronized List<String> getRepositories()
    {
        String fileDbDirectory = kurjunProperties.get( DbFilePackageMetadataStoreModule.DB_FILE_LOCATION_NAME );

        File fileDirectory = new File( fileDbDirectory );
        File[] files = fileDirectory.listFiles();

        List<String> results = new ArrayList<>();

        for ( File file : files )
        {
            if ( file.isDirectory() )
            {
                results.add( file.getName() );
            }
        }

        return results;
    }

    @Override
    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

}
