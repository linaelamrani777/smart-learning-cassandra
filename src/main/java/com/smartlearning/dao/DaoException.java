package com.smartlearning.dao;

/**
 * Exception générique de la couche DAO (enveloppe les erreurs du driver :
 * timeouts, indisponibilité des nœuds, etc.).
 * Si le Membre 2 fournit une exception équivalente, remplace-la ici.
 */
public class DaoException extends RuntimeException {
    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
