package org.javaboy.pagoda.framework.config;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

public class CustomMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {

        @Override
        protected MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, MethodInvocation invocation) {

                CustomSecurityExpressionRoot root = new CustomSecurityExpressionRoot(authentication);

                root.setPermissionEvaluator(getPermissionEvaluator());
                root.setTrustResolver(getTrustResolver());
                root.setRoleHierarchy(getRoleHierarchy());

                return root;
        }
}
