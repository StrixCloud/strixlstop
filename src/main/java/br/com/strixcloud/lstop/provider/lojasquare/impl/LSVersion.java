package br.com.strixcloud.lstop.provider.lojasquare.impl;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum LSVersion {

    LS_V1("https://api.lojasquare.com.br/v1/transacoes/topcompradores"),
    LS_V2("https://api.lojasquare.net/v1/transacoes/topcompradores");

    String url;

    public String getUrl() {
        return url;
    }
}
