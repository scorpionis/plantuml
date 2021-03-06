/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2014, Arnaud Roques
 *
 * Project Info:  http://plantuml.sourceforge.net
 * 
 * This file is part of PlantUML.
 *
 * PlantUML is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PlantUML distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * Original Author:  Arnaud Roques
 * 
 * Revision $Revision: 7696 $
 *
 */
package net.sourceforge.plantuml.ugraphic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class SlotSet implements Iterable<Slot> {

	private final List<Slot> all = new ArrayList<Slot>();

	public SlotSet filter(double start, double end) {
		final SlotSet result = new SlotSet();
		for (Slot slot : all) {
			final Slot intersec = slot.intersect(start, end);
			if (intersec != null) {
				result.all.add(intersec);
			}
		}
		return result;
	}

	public void addAll(SlotSet other) {
		this.all.addAll(other.all);
	}

	public void addSlot(double start, double end) {
		final List<Slot> collisions = new ArrayList<Slot>();
		Slot newSlot = new Slot(start, end);
		for (final Iterator<Slot> it = all.iterator(); it.hasNext();) {
			final Slot s = it.next();
			if (s.intersect(newSlot)) {
				it.remove();
				collisions.add(s);
			}
		}
		for (Slot s : collisions) {
			newSlot = newSlot.merge(s);
		}
		all.add(newSlot);
	}

	public SlotSet smaller(double margin) {
		final SlotSet result = new SlotSet();
		for (Slot sl : all) {
			if (sl.size() <= 2 * margin) {
				continue;
			}
			result.addSlot(sl.getStart() + margin, sl.getEnd() - margin);
		}
		return result;
	}

	@Override
	public String toString() {
		return all.toString();
	}

	public List<Slot> getSlots() {
		return Collections.unmodifiableList(all);
	}

	public Iterator<Slot> iterator() {
		return getSlots().iterator();
	}

	public SlotSet reverse() {
		final SlotSet result = new SlotSet();
		Collections.sort(all);
		Slot last = null;
		for (Slot slot : all) {
			if (last != null) {
				result.addSlot(last.getEnd(), slot.getStart());
			}
			last = slot;
		}
		return result;
	}

	public void drawDebugX(UGraphic ug, double size) {
		for (Slot slot : all) {
			final URectangle rect = new URectangle(slot.getEnd() - slot.getStart(), size);
			ug.apply(new UTranslate(slot.getStart(), 0)).draw(rect);
		}
	}

}
