package ai.subut.kurjun.web.service;


import java.util.List;
import java.util.Set;

import ai.subut.kurjun.model.identity.Permission;
import ai.subut.kurjun.model.identity.Relation;
import ai.subut.kurjun.model.identity.RelationObject;
import ai.subut.kurjun.model.identity.RelationObjectType;
import ai.subut.kurjun.model.identity.User;
import ai.subut.kurjun.model.identity.UserSession;

/**
 *
 */
public interface RelationManagerService extends BaseService {

    //*************************************
    List<Relation> getAllRelations();

    //*************************************
    void removeRelation( Relation relation );


    //*************************************
    void removeRelationsByTrustObject( String trustObjectId, int trustObjectType );


    Relation addTrustRelation(RelationObject source, RelationObject target, RelationObject trustObject,
                              Set<Permission> permissions);

    List<Relation> getTrustRelationsBySource(RelationObject sourceObject );


    List<Relation> getTrustRelationsByTarget(RelationObject targetObject );


    List<Relation> getTrustRelationsByObject(RelationObject trustObject );


    //*************************************
    Relation getRelation( String sourceId, String targetId, String trustObjectId, int trustObjectType );


    Relation getRelation( String relationId );


    //*************************************
    List<Relation> getRelationsByObject( String trustObjectId, int trustObjectType );


    //***************************
    Relation getObjectOwner( String trustObjectId, int trustObjectType );


    //***************************
    Relation buildTrustRelation( User sourceUser, User targetUser, String trustObjectId, int trustObjectType,
                                 Set<Permission> permissions );


    //***************************
    Set<Permission> buildPermissions( int permLevel );


    //***************************
    void checkRelationOwner( UserSession userSession, String objectId, int objectType );


    //***************************
    Set<Permission> checkUserPermissions( UserSession userSession, String objectId, int objectType );

    //*******************************************************************
    boolean checkRepoPermissions( UserSession userSession, String repoId, int repoType, String contentId,
                                  int contentType, Permission perm );

    RelationObject toSourceObject( User user );


    RelationObject toTargetObject( String fingerprint );


    RelationObject toTrustObject(UserSession userSession, String id, String md5, String name, String version, RelationObjectType rot );


    void saveRelation( Relation relation );

}
