package com.goxr3plus.xr3player.controllers.windows;

 import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

 /**
 * The Maven pom.xml file that is used to control the dependencies and build process of this project
 * has some platform dependent parts. Some dependencies are only valid for a specific platform.
 * These tests exist to check that the platform where the tests are run is supported by the
 * dependencies declared in pom.xml.
 */
public class IsPlatformSupported {

     @Test
    void isKnownOsName() {
        final List<String> validOsNames = Arrays.asList("mac os x", "windows");

         final String name = System.getProperty("os.name").toLowerCase(Locale.US);

         assertTrue(validOsNames.contains(name));
    }


     @Test
    void isKnownOsArchitecture() {
        final List<String> validOsArchitectures = Arrays.asList("x86_64", "some other arch");

         final String arch = System.getProperty("os.arch").toLowerCase(Locale.US);
        System.out.println(arch);

         assertTrue(validOsArchitectures.contains(arch));
    }

     @Test
    void isKnownOsVersion() {
        final List<String> validOsVersions = Arrays.asList("10.14.5", "some other version");

         final String version = System.getProperty( "os.version" ).toLowerCase( Locale.US );
        System.out.println(version);

         assertTrue(validOsVersions.contains(version));
    }


 }