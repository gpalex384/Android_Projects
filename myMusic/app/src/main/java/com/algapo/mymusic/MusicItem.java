package com.algapo.mymusic;

public class MusicItem {
    public final String id;
    public final String banda;
    public final String lp;
    public final String primer_single;
    public final String biografia;
    public final String anyo_publicacion;
    public final String discografica;
    public final String ciudad;
    public final String url_imagen_lp;
    public final String url_imagen_single;

    public MusicItem(String id, String banda, String lp, String primer_single, String biografia, String anyo_publicacion,
                     String discografica, String ciudad, String url_imagen_lp, String url_imagen_single) {
        this.id = id;
        this.banda = banda;
        this.lp = lp;
        this.primer_single = primer_single;
        this.biografia = biografia;
        this.anyo_publicacion = anyo_publicacion;
        this.discografica = discografica;
        this.ciudad = ciudad;
        this.url_imagen_lp = url_imagen_lp;
        this.url_imagen_single = url_imagen_single;
    }
    @Override
    public String toString() {
        return "\n\nBanda: " + this.banda + "\nSingle: " + this.primer_single +
            "\nCiudad: " + this.ciudad + "\nDiscográfica: " + this.discografica + "\n";
    }
}
