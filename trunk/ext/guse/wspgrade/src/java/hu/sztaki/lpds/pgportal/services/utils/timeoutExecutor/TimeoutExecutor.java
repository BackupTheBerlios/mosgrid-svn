/* Copyright 2007-2011 MTA SZTAKI LPDS, Budapest

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */
/*
 * This file is part of P-GRADE Grid Portal.
 *
 * P-GRADE Grid Portal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * P-GRADE Grid Portal is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * P-GRADE Grid Portal.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2006-2008 MTA SZTAKI
 *
 */
package hu.sztaki.lpds.pgportal.services.utils.timeoutExecutor;

public class TimeoutExecutor {

  public static final String RESULT_AT_TIMEOUT =
      "_T_I_M_E_O_U_T___E_X_P_I_R_E_D_";
  private Object result;
  private TimeoutThread timeoutT = null;
  private FunctionThread funcT = null;

  public TimeoutExecutor() {
    this.result = null;
  }

  protected synchronized void setResult(Object result) {
    if (this.result == null) {
      this.result = result;
    }
    notifyAll();
  }

  private synchronized Object getResult() {
    while (this.result == null) {
      try {
        wait();
      }
      catch (InterruptedException ie) {}
    }
    this.stopExecution();
    return this.result;
  }

  private void stopExecution() {
    this.timeoutT = null;
    this.funcT = null;
  }

  /**
   *
   * @param f 'Function' to be executed
   * @param timeout When it expires, we dont wait for the function's result.
   * @return the return value of f.
   * @throws FunctionTimeoutException when the timeout expires
   */
  public Object executeFunctionWithTimeout(Function f, long timeout) throws
      FunctionTimeoutException {
    this.timeoutT = new TimeoutThread(this, timeout);
    this.funcT = new FunctionThread(this, f);
    this.funcT.start();
    timeoutT.start();
    if (this.getResult().equals(TimeoutExecutor.RESULT_AT_TIMEOUT)) {
      throw new FunctionTimeoutException();
    }
    else {
      return result;
    }
  }
}
