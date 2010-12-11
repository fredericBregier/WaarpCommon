/**
   This file is part of GoldenGate Project (named also GoldenGate or GG).

   Copyright 2009, Frederic Bregier, and individual contributors by the @author
   tags. See the COPYRIGHT.txt in the distribution for a full listing of
   individual contributors.

   All GoldenGate Project is free software: you can redistribute it and/or 
   modify it under the terms of the GNU General Public License as published 
   by the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   GoldenGate is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with GoldenGate .  If not, see <http://www.gnu.org/licenses/>.
 */
package goldengate.common.exception;

/**
 * File Transfer exception (error during transfer from file point of
 * view)
 *
 * @author Frederic Bregier
 *
 */
public class FileTransferException extends Exception {

    /**
	 *
	 */
    private static final long serialVersionUID = 977343700748516315L;

    /**
     * @param message
     */
    public FileTransferException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public FileTransferException(String message, Throwable cause) {
        super(message, cause);
    }

}
