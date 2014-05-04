package com.github.parboiled1.grappa.cleanup;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.METHOD,  ElementType.FIELD, ElementType.TYPE })
public @interface WillBePrivate
{
    String version();
}