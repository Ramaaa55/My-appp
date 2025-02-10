import com.mysticmango.idealtattooia.R
import com.mysticmango.idealtattooia.TattooStyle

object TattooStyleProvider {
    fun defaultStyles(): List<TattooStyle> {
        return listOf(
            TattooStyle(R.drawable.estilo_japones, "JAPONÉS"),
            TattooStyle(R.drawable.estilo_realista, "REALISTA"),
            TattooStyle(R.drawable.estilo_tradicional, "TRADICIONAL"),
            TattooStyle(R.drawable.estilo_sagrado, "SAGRADO"),
            TattooStyle(R.drawable.estilo_futurista, "FUTURISTA")
        )
    }
}