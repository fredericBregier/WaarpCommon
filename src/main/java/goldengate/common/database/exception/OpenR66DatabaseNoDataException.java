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
package goldengate.common.database.exception;

/**
 * Database no data exception
 * @author Frederic Bregier
 *
 */
public class OpenR66DatabaseNoDataException extends OpenR66DatabaseException {

    /**
     *
     */
    private static final long serialVersionUID = -1148593385347608219L;

    /**
     *
     */
    public OpenR66DatabaseNoDataException() {
        super();
    }

    /**
     * @param arg0
     * @param arg1
     */
    public OpenR66DatabaseNoDataException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    /**
     * @param arg0
     */
    public OpenR66DatabaseNoDataException(String arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     */
    public OpenR66DatabaseNoDataException(Throwable arg0) {
        super(arg0);
    }

}