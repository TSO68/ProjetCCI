package com.example.projetcci;

import java.util.List;

public interface OnGetGenresCallback {

    void onSuccess(List<Genre> genres);

    void onError();
}
