package io.crnk.security;

import org.eclipse.jetty.ee10.servlet.security.ConstraintMapping;
import org.eclipse.jetty.ee10.servlet.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.*;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.util.security.Credential;

/**
 * A simple {@link IdentityManager} implementation, that just takes a map of users to their
 * password.
 * <p>
 * This is in now way suitable for real world production use.
 */
public class InMemoryIdentityManager {

	private final ConstraintSecurityHandler securityHandler;

	private final HashLoginService loginService;

	private final UserStore userStore;

	private final static String REALM = "myrealm";

	public InMemoryIdentityManager() {
		userStore = new UserStore();

		loginService = new HashLoginService();
		loginService.setName(REALM);
		loginService.setUserStore(userStore);

		securityHandler = new ConstraintSecurityHandler();
		securityHandler.setAuthenticator(new BasicAuthenticator());
		securityHandler.setRealmName(REALM);
		securityHandler.setLoginService(loginService);

		Constraint constraint = new Constraint.Builder()
				.name(Authenticator.BASIC_AUTH)
				.authorization(Constraint.Authorization.ANY_USER)
				.build();

		ConstraintMapping cm = new ConstraintMapping();
		cm.setConstraint(constraint);
		cm.setPathSpec("/*");
		securityHandler.addConstraintMapping(cm);
	}

	public void addUser(String userId, String password, String... roles) {
		userStore.addUser(userId, Credential.getCredential(password), roles);
		loginService.setUserStore(userStore);
	}

	public void clear() {
		securityHandler.getConstraintMappings().clear();
	}

	public SecurityHandler getSecurityHandler() {
		return securityHandler;
	}
}
