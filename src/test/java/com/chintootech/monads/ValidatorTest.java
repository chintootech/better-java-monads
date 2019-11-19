package com.chintootech.monads;

import org.junit.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * The type Validator test.
 */
public class ValidatorTest {

    /**
     * Enumeration of Types of Sex.
     */
    public enum Sex {
        /**
         * Male sex.
         */
        MALE,
        /**
         * Female sex.
         */
        FEMALE
    }

    /**
     * User Definition.
     */
    class User {

        private String name;
        private int age;
        private Sex sex;
        private String email;

        /**
         * Constructor.
         *
         * @param name  - name
         * @param age   - age
         * @param sex   - sex
         * @param email - email address
         */
        public User(String name, int age, Sex sex, String email) {
            this.name = name;
            this.age = age;
            this.sex = sex;
            this.email = email;
        }

        /**
         * Gets name.
         *
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * Gets age.
         *
         * @return the age
         */
        public int getAge() {
            return age;
        }

        /**
         * Gets sex.
         *
         * @return the sex
         */
        public Sex getSex() {
            return sex;
        }

        /**
         * Gets email.
         *
         * @return the email
         */
        public String getEmail() {
            return email;
        }
    }

    /**
     * Test for invalid name.
     */
    @Test
    public void testForInvalidName() {
        User tom = new User(null, 21, Sex.MALE, "tom@foo.bar");
        assertThrows(IllegalStateException.class, () -> {
            Validator.of(tom).validate(User::getName, Objects::nonNull, "name cannot be null").get();
        });
    }

    /**
     * Test for invalid age.
     */
    @Test
    public void testForInvalidAge() {
        User john = new User("John", 17, Sex.MALE, "john@qwe.bar");
        assertThrows(IllegalStateException.class, () -> {
            Validator.of(john).validate(User::getName, Objects::nonNull, "name cannot be null")
                    .validate(User::getAge, age -> age > 21, "user is underaged")
                    .get();
        });
    }

    /**
     * Test for valid.
     */
    @Test
    public void testForValid() {
        User sarah = new User("Sarah", 42, Sex.FEMALE, "sarah@det.org");
        User validated = Validator.of(sarah).validate(User::getName, Objects::nonNull, "name cannot be null")
                .validate(User::getAge, age -> age > 21, "user is underaged")
                .validate(User::getSex, sex -> sex == Sex.FEMALE, "user is not female")
                .validate(User::getEmail, email -> email.contains("@"), "email does not contain @ sign")
                .get();
        assertSame(validated, sarah);
    }
}


