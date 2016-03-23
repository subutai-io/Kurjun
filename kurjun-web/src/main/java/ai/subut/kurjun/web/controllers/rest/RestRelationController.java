package ai.subut.kurjun.web.controllers.rest;

import java.util.List;

import ai.subut.kurjun.identity.DefaultRelationObject;
import ai.subut.kurjun.model.identity.Relation;
import ai.subut.kurjun.web.controllers.BaseController;
import ai.subut.kurjun.web.service.IdentityManagerService;
import ninja.Result;
import ninja.Results;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import ai.subut.kurjun.model.identity.*;
import ai.subut.kurjun.web.security.AuthorizedUser;
import ai.subut.kurjun.web.service.RelationManagerService;
import ninja.params.Param;
import ninja.params.Params;
import ninja.params.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * REST Controller for Trust Relation Management
 */
@Singleton
public class RestRelationController extends BaseController
{
    private static final Logger LOGGER = LoggerFactory.getLogger( RestIdentityController.class );

    @Inject
    IdentityManagerService identityManagerservice;

    @Inject
    private RelationManagerService relationManagerService;

    @Inject
    private IdentityManagerService identityManagerService;


    public Result getAllRelations()
    {
        List<Relation> relations = relationManagerService.getAllRelations();

        return Results.ok().render( relations ).json();
    }


    public Result getRelationsByOwner( @PathParam( "fingerprint" ) String fingerprint )
    {
        return Results.ok().json().render( relationManagerService.getTrustRelationsBySource(
                relationManagerService.toSourceObject( identityManagerService.getUser( fingerprint ) ) ) );
    }


    public Result getRelationsByTarget( @PathParam( "fingerprint" ) String fingerprint )
    {
        return Results.ok().json().render( relationManagerService.getTrustRelationsByTarget(
                relationManagerService.toTargetObject(fingerprint) ) );
    }


    public Result getRelationsByObject( @PathParam( "id" ) String id,
                                        @Param( "name" ) String name, @Param( "version" ) String version,
                                        @Param( "md5" ) String md5, @Param( "rel_obj_type" ) int relObjType )
    {
        RelationObjectType objType = RelationObjectType.valueOf( relObjType );
        if ( objType == null ) {
            objType = RelationObjectType.RepositoryContent;
        }
        return Results.ok().json().render( relationManagerService.getTrustRelationsByObject(
                relationManagerService.toTrustObject(id, name, md5, version, objType)));
    }


    public Result addTrustRelation( @AuthorizedUser UserSession userSession, @Param( "fingerprint" ) String fingerprint,
                                    @Param( "id" ) String id, @Params( "permission" ) String[] permissions,
                                    @Param("trust_obj_type") int trustObjType )
    {
        RelationObject owner = relationManagerService.toSourceObject( userSession.getUser() );
        RelationObject target = relationManagerService.toTargetObject( fingerprint );

        RelationObjectType relObjType = RelationObjectType.RepositoryContent;
        if ( trustObjType == RelationObjectType.RepositoryTemplate.getId() ) // repository
        {
            relObjType = RelationObjectType.RepositoryTemplate;
        }
        RelationObject trustObject = relationManagerService
                    .toTrustObject( id, null, null, null, relObjType );

        Set<Permission> objectPermissions = new HashSet<>();
        Arrays.asList( permissions ).forEach( p -> objectPermissions.add(Permission.valueOf(p)) );

        Relation relation = relationManagerService.addTrustRelation(owner, target, trustObject, objectPermissions);
        if ( relation != null )
        {
            return Results.ok();
        }
        else
        {
            return Results.notFound();
        }
    }
}
