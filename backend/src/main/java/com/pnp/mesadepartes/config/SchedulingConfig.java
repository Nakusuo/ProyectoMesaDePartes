package com.pnp.mesadepartes.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Configuración de tareas programadas (Scheduling)
 * Habilita la ejecución de tareas automáticas mediante @Scheduled
 * 
 * @author Mesa de Partes Digital - PNP
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
    // Configuración básica para habilitar scheduling
    // Las tareas programadas se definen en los servicios con @Scheduled
}
