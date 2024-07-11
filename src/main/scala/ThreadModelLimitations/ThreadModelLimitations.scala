package ThreadModelLimitations

object ThreadModelLimitations extends App {
  var task: Runnable = null
  val runningThread: Thread = new Thread(() => {
    while (true)
      while (task == null) {
        runningThread.synchronized {
          println("[background] waiting for a task...")
          runningThread.wait()
        }

        task.synchronized {
          println("[background] I have a task!")
          task.run()
          task = null
        }
      }
  })

  def delegateToBackgroundThread(r: Runnable) = {
    if (task == null) task = r

    runningThread.synchronized {
      runningThread.notify()

    }
  }

  runningThread.start()
  Thread.sleep(1000)
  delegateToBackgroundThread(() => println(42))
  Thread.sleep(1000)
  delegateToBackgroundThread(() => println("this should run in the background"))

}
