package helpers;

import controllers.Authentication;
import models.*;
import play.libs.F;
import play.mvc.*;
import play.mvc.Http.*;

import java.lang.annotation.*;
import java.util.Arrays;
import java.util.List;


/**
 * Created by Freek on 19/05/14.
 * several security helpers for authentication
 */
public final class Secure {

    /**
     * Default private constructor to make sure it doesn't get created
     */
    private Secure() {
        //not called
    }

    /**
     * Wraps the annotated action in an <code>AuthenticatedAction</code>.
     */
    @With(AuthenticatedAction.class)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Authenticated {

        /**
         * The types of users that should be able to access the action
         */
        User.Type[] types() default {User.Type.USER, User.Type.TEACHER, User.Type.ADMIN};
    }

    /**
     * Wraps another action, allowing only authenticated HTTP requests.
     * <p>
     * The user name is retrieved from the session cookie, and added to the HTTP request's
     * <code>username</code> attribute.
     */
    public static class AuthenticatedAction extends Action<Authenticated> {

        /**
         * Handle the call
         * @param ctx The context of the call
         * @return A Result
         */
        public F.Promise<SimpleResult> call(Context ctx) {
            try {
                User user = Secure.getUser(ctx);
                List<User.Type> types = Arrays.asList(configuration.types());
                if(user == null || !types.contains(user.getType())) {
                    Result unauthorized = Authentication.onUnauthorized();
                    return F.Promise.pure((SimpleResult) unauthorized);
                } else {
                    try {
                        ctx.args.put("user", user);
                        return delegate.call(ctx);
                    }
                    finally {
                        ctx.request().setUsername(null);
                    }
                }
            }
            catch(RuntimeException e) {
                throw e;
            }
            catch(Throwable t) {
                throw new RuntimeException(t);
            }
        }

    }

    /**
     * Get the currently logged in user id
     * @param ctx The context of the call
     * @return The user id
     */
    public static Long getUserId(Context ctx) {
        String userId = ctx.session().get("user_id");
        return (userId == null)? null : Long.parseLong(userId);
    }

    /**
     * Get the currently logged in user
     * @param ctx The context of the call
     * @return The user
     */
    public static User getUser(Context ctx) {
        // Check if it exists in context
        if(ctx.args.containsKey("user")) {
            return (User) ctx.args.get("user");
        }

        // Else lookup the user
        Long userId = getUserId(ctx);
        return (userId == null)? null : User.findById(userId);
    }

    /**
     * Get the currently logged in user using the current context
     * @return The user
     */
    public static User getUser() {
        return Secure.getUser(Context.current());
    }

}
