package controllers;

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
        User.Type[] value() default {User.Type.User, User.Type.Teacher, User.Type.Admin};
    }

    /**
     * Wraps another action, allowing only authenticated HTTP requests.
     * <p>
     * The user name is retrieved from the session cookie, and added to the HTTP request's
     * <code>username</code> attribute.
     */
    public static class AuthenticatedAction extends Action<Authenticated> {

        /**
         * Check the login and return the correct Resulting page
         * @param ctx The current context
         * @param user The current user
         * @param types The type of users allowed
         * @return The Resulting page
         * @throws Throwable A possible delegate call throwable
         */
        private F.Promise<SimpleResult> checkLogin(Context ctx, User user, List<User.Type> types) throws Throwable {
            // Check if the user is logged in or doesn't have access
            if((user != null && !types.contains(user.getType()))
                    || (user == null && !types.contains(User.Type.Guest))) {
                if(user == null) {
                    return F.Promise.pure((SimpleResult) AuthenticationController.onUnauthorized());
                }
                else {
                    return F.Promise.pure((SimpleResult) AuthenticationController.onAuthorized());
                }
            }
            else {
                try {
                    ctx.args.put("user", user);
                    return delegate.call(ctx);
                }
                finally {
                    ctx.args.clear();
                }
            }
        }

        /**
         * Handle the call
         * @param ctx The context of the call
         * @return A Result
         */
        public F.Promise<SimpleResult> call(Context ctx) {
            try {
                String userId = ctx.session().get("user_id");
                User user = (userId == null)? null : User.findById(Long.parseLong(userId));
                List<User.Type> types = Arrays.asList(configuration.value());

                return checkLogin(ctx, user, types);
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

    /**
     * Check if the current user is a teacher
     * @return If the user is a teacher
     */
    public static boolean isTeacher() {
        User user = getUser();
        return (user != null && user.getType() == User.Type.Teacher);
    }

    /**
     * Check if the current user is an admin
     * @return If the user is an admin
     */
    public static boolean isAdmin() {
        User user = getUser();
        return (user != null && user.getType() == User.Type.Admin);
    }
}
