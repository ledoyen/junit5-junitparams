package com.github.ledoyen.junit5.junitparams.usage;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestReporter;

import com.github.ledoyen.junit5.junitparams.Parameters;
import com.github.ledoyen.junit5.junitparams.usage.person_example.PersonTest;
import com.github.ledoyen.junit5.junitparams.usage.person_example.PersonType;

public class SamplesOfUsageTest {

    @Parameters({"AAA,1", "BBB,2"})
    public void paramsInAnnotation(String p1, Integer p2, TestInfo info, TestReporter reporter) {
        reporter.publishEntry("paramsInAnnotation " + info.getDisplayName(), p1 + " " + p2);
    }

    @Parameters({"AAA|1", "BBB|2"})
    public void paramsInAnnotationPipeSeparated(String p1, Integer p2) { }

    @Parameters
    public void paramsInDefaultMethod(String p1, Integer p2) { }
    private Object parametersForParamsInDefaultMethod() {
        return new Object[]{new Object[]{"AAA", 1}, new Object[]{"BBB", 2}};
    }

//    @Parameters(method = "named1")
//    public void paramsInNamedMethod(String p1, Integer p2) { }
//    private Object named1() {
//        return new Object[]{"AAA", 1};
//    }
//
//    @Parameters(method = "named2,named3")
//    public void paramsInMultipleMethods(String p1, Integer p2) { }
//    private Object named2() {
//        return new Object[]{"AAA", 1};
//    }
//    private Object named3() {
//        return new Object[]{"BBB", 2};
//    }
//
//    @Parameters(method = "named4")
//    public void paramsWithVarargs(String... args) {
//        assertThat(args).isEqualTo(new String[]{"AAA", "BBB"});
//    }
//    private Object named4() { return new Object[]{new String[]{"AAA", "BBB"}}; }

    @Parameters
    public void paramsInCollection(String p1) { }
    private List<String> parametersForParamsInCollection() { return Arrays.asList("a"); }

    @Parameters
    public void paramsInIterator(String p1) { }
    private Iterator<String> parametersForParamsInIterator() { return Arrays.asList("a").iterator(); }

//    @Test
//    @Parameters
//    public void paramsInIterableOfIterables(String p1, String p2) { }
//    private List<List<String>> parametersForParamsInIterableOfIterables() {
//        return Arrays.asList(
//                Arrays.asList("s01e01", "s01e02"),
//                Arrays.asList("s02e01", "s02e02")
//        );
//    }
//
//    @Test
//    @Parameters({"SOME_VALUE", "OTHER_VALUE"})
//    public void enumsAsParamInAnnotation(PersonType person) { }
//
//    @Test
//    @Parameters
//    public void enumsAsParamsInMethod(PersonType person) { }
//    private PersonType[] parametersForEnumsAsParamsInMethod() { return (PersonType[]) new PersonType[] {PersonType.SOME_VALUE}; }
//
//    @Test
//    @Parameters
//    public void wrapParamsWithConstructor(PersonTest.Person person) { }
//    private Object parametersForWrapParamsWithConstructor() {
//        return new Object[]{new Object[]{"first", 1}, new Object[]{"second", 2}};
//    }
//
//    @Test
//    @Parameters("please\\, escape commas if you use it here and don't want your parameters to be splitted")
//    public void commasInParametersUsage(String phrase) { }
}
