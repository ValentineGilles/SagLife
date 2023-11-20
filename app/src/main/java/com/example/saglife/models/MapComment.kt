package com.example.saglife.models

import java.util.Date

/**
 * Modèle de données représentant un commentaire sur une carte.
 *
 * @param author Auteur du commentaire.
 * @param comment Contenu du commentaire.
 * @param date Date à laquelle le commentaire a été créé.
 * @param note Note attribuée au commentaire.
 */
class MapComment (val author: String, val comment : String, val date : Date, val note : Int) {

    /**
     * Obtient le jour du mois et le mois formaté de la date du commentaire.
     *
     * @return Chaîne représentant le jour du mois et le mois.
     */
    fun getDay(): String{
        return date.date.toString() + " "+ getMonth()
    }

    /**
     * Obtient le mois formaté de la date du commentaire.
     *
     * @return Chaîne représentant le mois.
     */
    private fun getMonth() : String{
        return when(date.month){
            0-> "Janv."
            1-> "Févr."
            2-> "Mars"
            3-> "Avr."
            4-> "Mai"
            5-> "Juin"
            6-> "Juil."
            7-> "Août"
            8-> "Sept."
            9-> "Oct."
            10-> "Nov."
            else-> "Déc."
        }
    }
}