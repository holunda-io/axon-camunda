package io.holunda.axon.camunda.spring

import org.springframework.context.SmartLifecycle

/**
 * Opinionated best guess implementation of [SmartLifecycle], just override [onStart] and  [getPhase]
 * to get an auto-started, startable and stoppable lifecycle manager.
 */
abstract class DefaultSmartLifecycle(val thePhase: Int) : SmartLifecycle {

  private var running: Boolean = false

  override fun start() {
    onStart()
    this.running = true
  }

  abstract fun onStart()

  override fun isAutoStartup() = true

  override fun stop(callback: Runnable?) {
    callback?.run()
    this.running = false
  }

  override fun stop() = stop(null)

  override fun isRunning() = running

  override fun getPhase(): Int = thePhase
}
