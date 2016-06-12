/**
 * This feature manage an unique id associated to a device (Arduino based board).
 * It works with custom messages sent to Arduino looking up for a uid setted. If it
 * doesn't exist then it is generated and sent to Arduino based board (that should
 * persists it for example in its EEPROM)
 */
package org.ardulink.extendedfeatures.deviceid;