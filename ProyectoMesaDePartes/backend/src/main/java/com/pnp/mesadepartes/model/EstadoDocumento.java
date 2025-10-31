package com.pnp.mesadepartes.model;

public enum EstadoDocumento {
    Asignado,    // Estado inicial cuando se registra y asigna
    Recibido,    // El trabajador ha recibido el documento
    En_Proceso,  // El trabajador está procesando el documento
    Observado,   // Documento con observaciones
    Finalizado,  // Trámite completado con informe
    Salida       // Documento ha salido del sistema
}