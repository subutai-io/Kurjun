package ai.subut.kurjun.web.controllers.rest;


import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.model.identity.User;
import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.web.controllers.BaseController;
import ai.subut.kurjun.web.filter.SecurityFilter;
import ai.subut.kurjun.web.service.IdentityManagerService;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.session.FlashScope;


/**
 * REST Controller for Identity Management
 */
@Singleton
public class RestIdentityController extends BaseController
{
    private static final Logger LOGGER = LoggerFactory.getLogger( RestIdentityController.class );

    @Inject
    IdentityManagerService identityManagerService;


    //*************************
    public Result getUsers()
    {
        List<User> users = identityManagerService.getAllUsers();
        List<String> userNames = new ArrayList<>();

        if(!users.isEmpty())
        {
            for(User user: users)
            {
                userNames.add( user.getKeyFingerprint());
            }
        }

        return Results.ok().render( userNames ).json();
    }


    //*************************
    public Result getUser( @Param( "fingerprint" ) String fingerprint )
    {

        User user = identityManagerService.getUser( fingerprint );

        if(user != null)
        {
            return Results.ok().render( user.getKeyFingerprint() ).json();
        }
        else
        {
            return Results.notFound().text().render( "User not found" );
        }

    }


    public Result getActiveUser( Context context )
    {
        UserSession userSession = (UserSession) context.getAttribute(SecurityFilter.USER_SESSION);

        if ( userSession != null )
        {
            return Results.ok().json().render(userSession.getUser().getKeyFingerprint());
        }

        return Results.notFound().text().render( "Active user not found" );
    }


    //*************************
    public Result addUser( @Param( "key" ) String publicKey )
    {
        try
        {
            User user = identityManagerService.addUser( publicKey );

            if ( user != null )
            {
                return Results.ok().render( user.getSignature() ).text();
            }
            else
            {
                return Results.badRequest().text().render( "Failed to add user's key" );
            }
        }
        catch ( Exception e )
        {
            LOGGER.error( "Failed to add user: "+e.getMessage() );
            return Results.internalServerError().text().render( e.getMessage() );
        }
    }


    //*************************
    public Result authorizeUser(@Param( "fingerprint" ) String fingerprint, @Param( "message" ) String message,
                                Context context, FlashScope flashScope )
    {
        try
        {
            User user = identityManagerService.authenticateUser( fingerprint, message );

            if ( user != null )
            {
                context.getSession().put( SecurityFilter.USER_SESSION, user.getUserToken().getFullToken() );
                return Results.ok().render( user.getUserToken().getFullToken() ).text();
            }
            else
            {
                return Results.notFound().text().render( "User not found" );
            }
        }
        catch ( Exception e )
        {
            LOGGER.error( "Failed to authorize user: "+e.getMessage() );
            return Results.badRequest().text().render( e.getMessage() );
        }
    }


    //*************************
    public Result setSystemOwner(@Param( "fingerprint" ) String fingerprint, @Param( "key" ) String key )
    {
        User user = identityManagerService.setSystemOwner(fingerprint,key);

        if (user != null)
        {
            return Results.ok().render( user.getKeyFingerprint() ).json();
        }
        else
        {
            return Results.internalServerError();
        }
    }


    //*************************
    public Result getSystemOwner()
    {
        User user = identityManagerService.getSystemOwner();

        if (user != null)
        {
            return Results.ok().render( user.getKeyFingerprint() ).json();
        }
        else
        {
            return Results.internalServerError();
        }
    }
}
