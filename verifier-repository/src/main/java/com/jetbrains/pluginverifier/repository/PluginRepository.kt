package com.jetbrains.pluginverifier.repository

import com.intellij.structure.ide.IdeVersion
import java.io.Closeable
import java.io.File

interface PluginRepository {

  fun getLastCompatibleUpdates(ideVersion: IdeVersion): List<UpdateInfo>

  fun getLastCompatibleUpdateOfPlugin(ideVersion: IdeVersion, pluginId: String): UpdateInfo?

  fun getAllCompatibleUpdatesOfPlugin(ideVersion: IdeVersion, pluginId: String): List<UpdateInfo>

  fun getPluginFile(update: UpdateInfo): FileLock?

  fun getUpdateInfoById(updateId: Int): UpdateInfo?

}

abstract class FileLock : Closeable {

  abstract fun getFile(): File

  abstract fun release()

  override fun close(): Unit = release()
}

data class IdleFileLock(val content: File) : FileLock() {
  override fun getFile(): File = content

  override fun release() {
    //do nothing.
  }

}