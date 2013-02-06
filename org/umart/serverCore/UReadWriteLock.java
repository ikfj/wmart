package org.umart.serverCore;

/**
 * Read-Write Lockパターンを実装したクラスです．
 * @author 川部 裕司
 * @author 森下 領平
 * @author 小野 功
 */
public final class UReadWriteLock {

  /** 現在, 読み出しを行っているReaderスレッドの数 */
  private int fReadingReaders = 0;

  /** 現在, 待機中のWriterスレッドの数 */
  private int fWaitingWriters = 0;

  /** 現在, 書き込み中のスレッドの数 */
  private int fWritingWriters = 0;

  /** 書き込み優先か? */
  private boolean fPreferWriter = true;

  /**
   * Read Lockを取得する。
   */
  public synchronized void readLock() throws InterruptedException {
    while (fWritingWriters > 0
           || (fPreferWriter && fWaitingWriters > 0)) {
      wait();
    }
    ++fReadingReaders;
  }

  /**
   * Read Lockを解放する。
   */
  public synchronized void readUnlock() {
    --fReadingReaders;
    fPreferWriter = true;
    notifyAll();
  }

  /**
   * Write Lockを取得する。
   */
  public synchronized void writeLock() throws InterruptedException {
    ++fWaitingWriters;
    try {
      while (fReadingReaders > 0 || fWritingWriters > 0) {
        wait();
      }
    } finally {
      --fWaitingWriters;
    }
    ++fWritingWriters;
  }

  /**
   * Write Lockを解放する。
   */
  public synchronized void writeUnlock() {
    --fWritingWriters;
    fPreferWriter = false;
    notifyAll();
  }
}
