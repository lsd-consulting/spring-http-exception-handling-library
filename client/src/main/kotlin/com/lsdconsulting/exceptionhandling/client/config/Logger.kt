package com.lsdconsulting.exceptionhandling.client.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory

inline fun <reified T> T.log(): Logger {
    return LoggerFactory.getLogger(T::class.java)
}