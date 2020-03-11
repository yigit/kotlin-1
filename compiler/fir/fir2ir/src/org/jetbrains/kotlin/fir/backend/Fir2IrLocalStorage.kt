/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.backend

import org.jetbrains.kotlin.fir.declarations.FirClass
import org.jetbrains.kotlin.fir.declarations.FirFunction
import org.jetbrains.kotlin.fir.declarations.FirValueParameter
import org.jetbrains.kotlin.fir.declarations.FirVariable
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.declarations.IrVariable

class Fir2IrLocalStorage {

    private val cacheStack = mutableListOf<Fir2IrCallableCache>()

    private val localClassCache = mutableMapOf<FirClass<*>, IrClass>()

    fun enterCallable() {
        cacheStack += Fir2IrCallableCache()
    }

    fun leaveCallable() {
        cacheStack.last().clear()
        cacheStack.removeAt(cacheStack.size - 1)
    }

    fun getParameter(parameter: FirValueParameter): IrValueParameter? {
        for (cache in cacheStack.asReversed()) {
            val local = cache.getParameter(parameter)
            if (local != null) return local
        }
        return null
    }

    fun getVariable(variable: FirVariable<*>): IrVariable? {
        for (cache in cacheStack.asReversed()) {
            val local = cache.getVariable(variable)
            if (local != null) return local
        }
        return null
    }

    fun getLocalClass(localClass: FirClass<*>): IrClass? {
        return localClassCache[localClass]
    }

    fun getLocalFunction(localFunction: FirFunction<*>): IrSimpleFunction? {
        for (cache in cacheStack.asReversed()) {
            val local = cache.getLocalFunction(localFunction)
            if (local != null) return local
        }
        return null
    }

    fun putParameter(firParameter: FirValueParameter, irParameter: IrValueParameter) {
        cacheStack.last().putParameter(firParameter, irParameter)
    }

    fun putVariable(firVariable: FirVariable<*>, irVariable: IrVariable) {
        cacheStack.last().putVariable(firVariable, irVariable)
    }

    fun putLocalClass(firClass: FirClass<*>, irClass: IrClass) {
        localClassCache[firClass] = irClass
    }

    fun putLocalFunction(firFunction: FirFunction<*>, irFunction: IrSimpleFunction) {
        cacheStack.last().putLocalFunction(firFunction, irFunction)
    }
}