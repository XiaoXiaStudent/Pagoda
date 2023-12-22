package org.javaboy.pagoda.framework.config;

import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.AntPathMatcher;

import java.util.Collection;

public class CustomSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

        private Object filterObject;
        private Object returnObject;

        AntPathMatcher matcher = new AntPathMatcher();

        public boolean hasPermissions(String permission) {
                Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

                if (permission == null || permission == "") {
                        return false;
                }

                for (GrantedAuthority authority : authorities) {
                        if (matcher.match(authority.getAuthority(), permission)) {
                                return true;
                        }
                }
                return false;
        }

        public boolean hasAnyPermissions(String... permission) {

                if (permission == null || permission.length == 0) {
                        return false;
                }

                Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
                for (GrantedAuthority authority : authorities) {
                        for (String p : permission) {
                                if (matcher.match(authority.getAuthority(), p)) {
                                        return true;
                                }
                        }
                }
                return false;
        }

        public boolean hasAllPermissions(String... permission) {

                if (permission == null || permission.length == 0) {
                        return false;
                }
                boolean flag = false;

                Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
                for (GrantedAuthority authority : authorities) {
                        for (String p : permission) {
                                if (matcher.match(authority.getAuthority(), p)) {

                                        flag = true;
                                } else {
                                        return false;
                                }

                        }
                }

                return flag;
        }

        public CustomSecurityExpressionRoot(Authentication authentication) {
                super(authentication);
        }

        @Override
        public void setFilterObject(Object o) {
                this.filterObject = o;
        }

        @Override
        public Object getFilterObject() {
                return this.filterObject;
        }

        @Override
        public void setReturnObject(Object o) {
                this.returnObject = o;
        }

        @Override
        public Object getReturnObject() {
                return this.returnObject;
        }

        @Override
        public Object getThis() {
                return this;
        }

}
