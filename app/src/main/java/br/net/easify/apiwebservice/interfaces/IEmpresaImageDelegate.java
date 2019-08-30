package br.net.easify.apiwebservice.interfaces;

import android.net.Uri;

public interface IEmpresaImageDelegate {
    void onEmpresaImage(boolean success, Uri uri, int position);
}
