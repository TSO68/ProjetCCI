package com.example.projetcci;

import com.example.projetcci.utils.Validator;

import org.junit.Test;
import static com.google.common.truth.Truth.assertThat;

public class ValidatorUnitTest {

    @Test
    public void email_isCorrect() {
        assertThat(Validator.checkEmail("testeur@gmail.com")).isTrue();
    }

    @Test
    public void email_isFalse() {
        assertThat(Validator.checkPassword("")).isFalse();
        assertThat(Validator.checkEmail("testeurgmail.com")).isFalse();
    }

    @Test
    public void password_isCorrect() {
        assertThat(Validator.checkPassword("testeur1")).isTrue();
    }

    @Test
    public void password_isFalse() {
        assertThat(Validator.checkPassword("")).isFalse();
        assertThat(Validator.checkPassword("test")).isFalse();
    }

    @Test
    public void confirmPassword_isCorrect() {
        assertThat(Validator.checkConfirmPassword("testeur1", "testeur1")).isTrue();
    }

    @Test
    public void confirmPassword_isFalse() {
        assertThat(Validator.checkConfirmPassword("", "")).isFalse();
        assertThat(Validator.checkConfirmPassword("testeur1","test")).isFalse();
    }

    @Test
    public void content_isCorrect() {
        assertThat(Validator.checkContent("George Abitbol")).isTrue();
    }

    @Test
    public void content_isFalse() {
        assertThat(Validator.checkContent("")).isFalse();
    }
}
