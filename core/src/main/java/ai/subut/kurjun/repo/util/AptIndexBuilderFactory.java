package ai.subut.kurjun.repo.util;


import ai.subut.kurjun.common.KurjunContext;
import ai.subut.kurjun.repo.service.PackagesIndexBuilder;


/**
 * Factory interface to create apt index file builders like release index file or packages index file.
 *
 */
public interface AptIndexBuilderFactory
{

    /**
     * Creates packages index file builder for the supplied context.
     *
     * @param context
     * @return
     */
    PackagesIndexBuilder createPackagesIndexBuilder( KurjunContext context );


    /**
     * Created release index builder for the supplied context.
     *
     * @param context
     * @return release index builder
     */
    ReleaseIndexBuilder createReleaseIndexBuilder( KurjunContext context );
}

