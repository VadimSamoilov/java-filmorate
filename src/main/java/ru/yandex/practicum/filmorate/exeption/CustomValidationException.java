package ru.yandex.practicum.filmorate.exeption;

public class CustomValidationException extends RuntimeException{

    public CustomValidationException(String s){
        super(s);
    }
}
