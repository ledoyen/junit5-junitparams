package com.github.ledoyen.junit5.junitparams.usage.person_example;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.github.ledoyen.junit5.junitparams.Parameters;

public class PersonTest {

    @Parameters({
            "17, false",
            "22, true"})
    public void isAdultAgeDirect(int age, boolean valid) throws Exception {
        assertThat(new Person(age).isAdult()).isEqualTo(valid);
    }

    @Test
    @Parameters(method = "adultValues")
    public void isAdultAgeDefinedMethod(int age, boolean valid) throws Exception {
        assertThat(new Person(age).isAdult()).isEqualTo(valid);
    }

    private Object[] adultValues() {
        return new Object[]{new Object[]{17, false}, new Object[]{22, true}};
    }

    @Parameters
    public void isAdultAgeDefaultMethod(int age, boolean valid) throws Exception {
        assertThat(new Person(age).isAdult()).isEqualTo(valid);
    }

    @SuppressWarnings("unused")
    private Object[] parametersForIsAdultAgeDefaultMethod() {
        return adultValues();
    }

    @Parameters(method = "adultValues")
//    @TestCaseName("Is person with age {0} adult? It's {1} statement.")
    public void isAdultWithCustomTestName(int age, boolean valid) throws Exception {
        assertThat(new Person(age).isAdult()).isEqualTo(valid);
    }

    public static class Person {

        private String name;
        private int age;

        public Person(Integer age) {
            this.age = age;
        }

        public Person(String name, Integer age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public boolean isAdult() {
            return age >= 18;
        }

        public int getAge() {
            return age;
        }

        @Override
        public String toString() {
            return "Person of age: " + age;
        }
    }
}
