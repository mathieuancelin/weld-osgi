/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.cdi.test.extension;

import junit.framework.Assert;
import org.junit.Test;
import org.osgi.cdi.impl.extension.FilterGenerator;

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;
import java.lang.annotation.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.annotation.ElementType.*;

public class FilterGeneratorTest {

    @Test
    public void filterTest() {

        Assert.assertEquals("makeFilter() was wrong", "", FilterGenerator.makeFilter().value());

        String tokA = "token1", tokB = "token2", tokC = "token2";
        Set<String> set1 = new HashSet<String>(),
                set2 = new HashSet<String>(),
                set3 = new HashSet<String>(),
                set0 = new HashSet<String>();
        set1.add(tokA); set2.add(tokA); set3.add(tokA);
        set2.add(tokB); set3.add(tokB);
        set3.add(tokC);

        Assert.assertEquals("make(set0) was wrong", "", FilterGenerator.make(set0).value());
        Assert.assertEquals("make(set1) was wrong","token1", FilterGenerator.make(set1).value());
        Assert.assertEquals("make(set2) was wrong","(&token1token2)", FilterGenerator.make(set2).value());
        Assert.assertEquals("make(set3) was wrong","(&token1token2)", FilterGenerator.make(set3).value());

        Assert.assertEquals("makeFilter(tokA) was wrong","token1", FilterGenerator.makeFilter(tokA).value());
        Assert.assertEquals("makeFilter(tokA + tokB) was wrong","token1token2", FilterGenerator.makeFilter(tokA + tokB).value());

        NonQualifierAnnotation nonQualifierAnnotation = new NonQualifierAnnotationLiteral();
        QualifierAnnotation qualifierAnnotation = new QualifierAnnotationLiteral();
        QualifierWithValueAnnotation qualifierWithValueAnnotation = new QualifierWithValueAnnotationLiteral("QWVA");
        QualifierWithDefaultValueAnnotation qualifierWithDefaultValueAnnotation = new QualifierWithDefaultValueAnnotationLiteral();
        QualifierWithValuesAnnotation qualifierWithValuesAnnotation = new QualifierWithValuesAnnotationLiteral("QWVsA");

        List<Annotation> list1 = new ArrayList<Annotation>(),
                list2 = new ArrayList<Annotation>(),
                list3 = new ArrayList<Annotation>(),
                list4 = new ArrayList<Annotation>(),
                list5 = new ArrayList<Annotation>(),
                list6 = new ArrayList<Annotation>(),
                list7 = new ArrayList<Annotation>(),
                list8 = new ArrayList<Annotation>(),
                list0 = new ArrayList<Annotation>();
        list1.add(nonQualifierAnnotation);
        list2.add(qualifierAnnotation);
        list3.add(qualifierWithValueAnnotation);
        list4.add(qualifierWithDefaultValueAnnotation);
        list5.add(qualifierWithValuesAnnotation);
        list6.add(qualifierWithValueAnnotation); list6.add(qualifierWithValueAnnotation);
        list7.add(qualifierWithValueAnnotation); list7.add(qualifierWithDefaultValueAnnotation);
        list8.add(nonQualifierAnnotation); list8.add(qualifierAnnotation); list8.add(qualifierWithValueAnnotation);
        list8.add(qualifierWithDefaultValueAnnotation); list8.add(qualifierWithValuesAnnotation);

        Assert.assertEquals("makeFilter(list0) was wrong","", FilterGenerator.makeFilter(list0).value());
        Assert.assertEquals("makeFilter(list1) was wrong","", FilterGenerator.makeFilter(list1).value());
        Assert.assertEquals("makeFilter(list2) was wrong","(qualifierannotation=*)", FilterGenerator.makeFilter(list2).value());
        Assert.assertEquals("makeFilter(list3) was wrong","(qualifierwithvalueannotation.value=QWVA)", FilterGenerator.makeFilter(list3).value());
        Assert.assertEquals("makeFilter(list4) was wrong","(qualifierwithdefaultvalueannotation.value=default)", FilterGenerator.makeFilter(list4).value());
        Assert.assertEquals("makeFilter(list5) was wrong","(&(qualifierwithvaluesannotation.value=QWVsA)(qualifierwithvaluesannotation.name=name))", FilterGenerator.makeFilter(list5).value());
        Assert.assertEquals("makeFilter(list6) was wrong","(qualifierwithvalueannotation.value=QWVA)", FilterGenerator.makeFilter(list6).value());
        Assert.assertEquals("makeFilter(list7) was wrong","(&(qualifierwithvalueannotation.value=QWVA)(qualifierwithdefaultvalueannotation.value=default))", FilterGenerator.makeFilter(list7).value());
        Assert.assertEquals("makeFilter(list8) was wrong","(&(qualifierwithvaluesannotation.value=QWVsA)(qualifierwithvaluesannotation.name=name)(qualifierannotation=*)(qualifierwithvalueannotation.value=QWVA)(qualifierwithdefaultvalueannotation.value=default))", FilterGenerator.makeFilter(list8).value());
    }

    @Target({TYPE, METHOD, PARAMETER, FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface NonQualifierAnnotation {
    }

    public class NonQualifierAnnotationLiteral
            extends AnnotationLiteral<NonQualifierAnnotation>
            implements NonQualifierAnnotation {
    }

    @Target({TYPE, METHOD, PARAMETER, FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Qualifier
    public @interface QualifierAnnotation {
    }

    public class QualifierAnnotationLiteral
            extends AnnotationLiteral<QualifierAnnotation>
            implements QualifierAnnotation {
    }

    @Target({TYPE, METHOD, PARAMETER, FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Qualifier
    public @interface QualifierWithValueAnnotation {
        String value();
    }

    public class QualifierWithValueAnnotationLiteral
            extends AnnotationLiteral<QualifierWithValueAnnotation>
            implements QualifierWithValueAnnotation {
        String value;

        public QualifierWithValueAnnotationLiteral(String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return value;
        }
    }

    @Target({TYPE, METHOD, PARAMETER, FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Qualifier
    public @interface QualifierWithDefaultValueAnnotation {
        String value() default "default";
    }

    public class QualifierWithDefaultValueAnnotationLiteral
            extends AnnotationLiteral<QualifierWithDefaultValueAnnotation>
            implements QualifierWithDefaultValueAnnotation {
        String value;

        public QualifierWithDefaultValueAnnotationLiteral() {
            this.value = "default";
        }

        public QualifierWithDefaultValueAnnotationLiteral(String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return value;
        }
    }

    @Target({TYPE, METHOD, PARAMETER, FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Qualifier
    public @interface QualifierWithValuesAnnotation {
        String value() default "default";
        String name() default "name";
    }

    public class QualifierWithValuesAnnotationLiteral
            extends AnnotationLiteral<QualifierWithValuesAnnotation>
            implements QualifierWithValuesAnnotation {
        String value;
        String name;

        public QualifierWithValuesAnnotationLiteral() {
            this.value = "default";
        }

        public QualifierWithValuesAnnotationLiteral(String value) {
            this.value = value;
        }

        public QualifierWithValuesAnnotationLiteral(String value, String name) {
            this.value = value;
            this.name = name;
        }

        @Override
        public String value() {
            return value;
        }

        @Override
        public String name() {
            return name;
        }
    }
}
