/*
 * Copyright (c) 2019, to individual contributors by the @author tags.
 * See the COPYRIGHT.txt in the distribution for a full listing of individual contributors.
 *
 * This file is part of Waarp Project.
 *
 * All Waarp Project is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Waarp is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Waarp . If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.waarp.common.future;

import io.netty.channel.Channel;

/**
 * Future that hold a channel as result
 * 
 * @author Frederic Bregier
 * 
 */
public class WaarpChannelFuture extends WaarpFuture {
    /**
     * Channel as result
     */
    private Channel channel = null;

    /**
     *
     */
    public WaarpChannelFuture() {
        super();
    }

    /**
     * @param cancellable
     */
    public WaarpChannelFuture(boolean cancellable) {
        super(cancellable);
    }

    /**
     * @return the channel as result
     */
    public Channel channel() {
        return channel;
    }

    /**
     * @param channel
     *            the channel to set
     */
    public void setChannel(Channel channel) {
        this.channel = channel;
    }

}
