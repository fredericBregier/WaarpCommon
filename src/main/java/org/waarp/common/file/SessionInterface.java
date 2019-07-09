/*******************************************************************************
 * This file is part of Waarp Project (named also Waarp or GG).
 *
 *  Copyright (c) 2019, Waarp SAS, and individual contributors by the @author
 *  tags. See the COPYRIGHT.txt in the distribution for a full listing of
 *  individual contributors.
 *
 *  All Waarp Project is free software: you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or (at your
 *  option) any later version.
 *
 *  Waarp is distributed in the hope that it will be useful, but WITHOUT ANY
 *  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 *  A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with
 *  Waarp . If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.waarp.common.file;

/**
 * Interface for SessionInterface
 *
 * @author Frederic Bregier
 */
public interface SessionInterface {
  /**
   * @return the DirInterface
   */
  public DirInterface getDir();

  /**
   * @return the AuthInterface
   */
  public AuthInterface getAuth();

  /**
   * Clean the session
   */
  public void clear();

  /**
   * @return the BlockSize to use in FileInterface operations
   */
  public int getBlockSize();

  /**
   * @return the configuration
   */
  public FileParameterInterface getFileParameter();

  /**
   * @return the Restart
   */
  public Restart getRestart();

  /**
   * @return the extension to give to Unique File (STOU)
   */
  public String getUniqueExtension();
}
