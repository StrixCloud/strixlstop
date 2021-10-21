package br.com.strixcloud.lstop.provider.lojasquare.impl;

import br.com.strixcloud.lstop.entities.data.TopAccount;
import br.com.strixcloud.lstop.provider.lojasquare.ILSProvider;
import br.com.strixcloud.lstop.provider.request.IRequestProvider;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import lombok.RequiredArgsConstructor;
import lombok.var;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class HttpLSProvider implements ILSProvider {

    private final IRequestProvider provider;
    protected final String url;

    public HttpLSProvider(IRequestProvider provider, String authKey, LSVersion version) {
        this.provider = provider;
        provider.auth(authKey);
        this.url = version.getUrl();
    }

    @Override
    public List<TopAccount> getAccounts() throws IOException {
        List<TopAccount> topAccounts = new ArrayList<>();
        var res = provider
                .get(url);

        // Gambiarra anti erro kkkk, de certa forma funciona bem.
        // Gambiarra a gente aceita, oque não aceitamos é a derrota.

        if (res.getOBJECT() instanceof JsonArray) return topAccounts;

        for (Map.Entry<String, JsonElement> entry : res.getOBJECT().getAsJsonObject().entrySet()) {
            var p = entry.getKey();
            var value = entry.getValue().getAsDouble();

            var acc = new TopAccount(p, value);
            topAccounts.add(acc);
        }
        return topAccounts;
    }
}
