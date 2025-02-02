/*******************************************************************************
 * Copyright (c) 2006, 2025 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.compare.tests;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.compare.internal.DocLineComparator;
import org.eclipse.compare.internal.core.TextLineLCS;
import org.eclipse.compare.rangedifferencer.IRangeComparator;
import org.eclipse.compare.rangedifferencer.RangeDifference;
import org.eclipse.compare.rangedifferencer.RangeDifferencer;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.junit.jupiter.api.Test;

public class DiffTest {

	private static final String ABC = "abc"; //$NON-NLS-1$
	private static final String DEF = "def"; //$NON-NLS-1$
	// private static final String BAR= "bar"; //$NON-NLS-1$
	// private static final String FOO= "foo"; //$NON-NLS-1$
	private static final String XYZ = "xyz"; //$NON-NLS-1$
	private static final String _123 = "123"; //$NON-NLS-1$
	// private static final String _456= "456"; //$NON-NLS-1$

	static final String SEPARATOR = System.lineSeparator();

	@Test
	public void testLineAddition() {
		String s1 = ABC + SEPARATOR + DEF + SEPARATOR + XYZ;
		String s2 = ABC + SEPARATOR + DEF + SEPARATOR + _123 + SEPARATOR + XYZ;
		TextLineLCS.TextLine[] l1 = TextLineLCS.getTextLines(s1);
		TextLineLCS.TextLine[] l2 = TextLineLCS.getTextLines(s2);
		TextLineLCS lcs = new TextLineLCS(l1, l2);
		lcs.longestCommonSubsequence(SubMonitor.convert(null, 100));
		TextLineLCS.TextLine[][] result = lcs.getResult();
		assertThat(result[0]).hasSameSizeAs(result[1]).hasSize(3);
		for (int i = 0; i < result[0].length; i++) {
			int index = i;
			assertThat(result[0][i]).matches(it -> it.sameText(result[1][index]), "same text at index " + index);
		}
		assertThat(result[0][0].lineNumber()).isEqualTo(0);
		assertThat(result[1][0].lineNumber()).isEqualTo(0);
		assertThat(result[0][1].lineNumber()).isEqualTo(1);
		assertThat(result[1][1].lineNumber()).isEqualTo(1);
		assertThat(result[0][2].lineNumber()).isEqualTo(2);
		assertThat(result[1][2].lineNumber()).isEqualTo(3);
	}

	@Test
	public void testLineDeletion() {
		String s1 = ABC + SEPARATOR + DEF + SEPARATOR + _123 + SEPARATOR + XYZ;
		String s2 = ABC + SEPARATOR + DEF + SEPARATOR + XYZ;
		TextLineLCS.TextLine[] l1 = TextLineLCS.getTextLines(s1);
		TextLineLCS.TextLine[] l2 = TextLineLCS.getTextLines(s2);
		TextLineLCS lcs = new TextLineLCS(l1, l2);
		lcs.longestCommonSubsequence(SubMonitor.convert(null, 100));
		TextLineLCS.TextLine[][] result = lcs.getResult();
		assertThat(result[0]).hasSameSizeAs(result[1]);
		for (int i = 0; i < result[0].length; i++) {
			int index = i;
			assertThat(result[0][i]).matches(it -> it.sameText(result[1][index]), "same text at index " + index);
		}
		assertThat(result[0][0].lineNumber()).isEqualTo(0);
		assertThat(result[0][1].lineNumber()).isEqualTo(1);
		assertThat(result[0][2].lineNumber()).isEqualTo(3);
		assertThat(result[1][0].lineNumber()).isEqualTo(0);
		assertThat(result[1][1].lineNumber()).isEqualTo(1);
		assertThat(result[1][2].lineNumber()).isEqualTo(2);
	}

	@Test
	public void testLineAppendEnd() {
		String s1 = ABC + SEPARATOR + DEF;
		String s2 = ABC + SEPARATOR + DEF + SEPARATOR + _123;
		TextLineLCS.TextLine[] l1 = TextLineLCS.getTextLines(s1);
		TextLineLCS.TextLine[] l2 = TextLineLCS.getTextLines(s2);
		TextLineLCS lcs = new TextLineLCS(l1, l2);
		lcs.longestCommonSubsequence(SubMonitor.convert(null, 100));
		TextLineLCS.TextLine[][] result = lcs.getResult();
		assertThat(result[0]).hasSameSizeAs(result[1]).hasSize(2);
		for (int i = 0; i < result[0].length; i++) {
			int index = i;
			assertThat(result[0][i]).matches(it -> it.sameText(result[1][index]), "same text at index " + index);
		}
		assertThat(result[0][0].lineNumber()).isEqualTo(0);
		assertThat(result[1][0].lineNumber()).isEqualTo(0);
		assertThat(result[0][1].lineNumber()).isEqualTo(1);
		assertThat(result[1][1].lineNumber()).isEqualTo(1);
	}

	@Test
	public void testLineDeleteEnd() {
		String s1 = ABC + SEPARATOR + DEF + SEPARATOR + _123;
		String s2 = ABC + SEPARATOR + DEF;
		TextLineLCS.TextLine[] l1 = TextLineLCS.getTextLines(s1);
		TextLineLCS.TextLine[] l2 = TextLineLCS.getTextLines(s2);
		TextLineLCS lcs = new TextLineLCS(l1, l2);
		lcs.longestCommonSubsequence(SubMonitor.convert(null, 100));
		TextLineLCS.TextLine[][] result = lcs.getResult();
		assertThat(result[0]).hasSameSizeAs(result[1]).hasSize(2);
		for (int i = 0; i < result[0].length; i++) {
			int index = i;
			assertThat(result[0][i]).matches(it -> it.sameText(result[1][index]), "same text at index " + index);
		}
		assertThat(result[0][0].lineNumber()).isEqualTo(0);
		assertThat(result[1][0].lineNumber()).isEqualTo(0);
		assertThat(result[0][1].lineNumber()).isEqualTo(1);
		assertThat(result[1][1].lineNumber()).isEqualTo(1);
	}

	@Test
	public void testLineAppendStart() {
		String s1 = ABC + SEPARATOR + DEF;
		String s2 = _123 + SEPARATOR + ABC + SEPARATOR + DEF;
		TextLineLCS.TextLine[] l1 = TextLineLCS.getTextLines(s1);
		TextLineLCS.TextLine[] l2 = TextLineLCS.getTextLines(s2);
		TextLineLCS lcs = new TextLineLCS(l1, l2);
		lcs.longestCommonSubsequence(SubMonitor.convert(null, 100));
		TextLineLCS.TextLine[][] result = lcs.getResult();
		assertThat(result[0]).hasSameSizeAs(result[1]).hasSize(2);
		for (int i = 0; i < result[0].length; i++) {
			int index = i;
			assertThat(result[0][i]).matches(it -> it.sameText(result[1][index]), "same text at index " + index);
		}
		assertThat(result[0][0].lineNumber()).isEqualTo(0);
		assertThat(result[1][0].lineNumber()).isEqualTo(1);
		assertThat(result[0][1].lineNumber()).isEqualTo(1);
		assertThat(result[1][1].lineNumber()).isEqualTo(2);
	}

	@Test
	public void testLineDeleteStart() {
		String s1 = _123 + SEPARATOR + ABC + SEPARATOR + DEF;
		String s2 = ABC + SEPARATOR + DEF;
		TextLineLCS.TextLine[] l1 = TextLineLCS.getTextLines(s1);
		TextLineLCS.TextLine[] l2 = TextLineLCS.getTextLines(s2);
		TextLineLCS lcs = new TextLineLCS(l1, l2);
		lcs.longestCommonSubsequence(SubMonitor.convert(null, 100));
		TextLineLCS.TextLine[][] result = lcs.getResult();
		assertThat(result[0]).hasSameSizeAs(result[1]).hasSize(2);
		for (int i = 0; i < result[0].length; i++) {
			int index = i;
			assertThat(result[0][i]).matches(it -> it.sameText(result[1][index]), "same text at index " + index);
		}
		assertThat(result[0][0].lineNumber()).isEqualTo(1);
		assertThat(result[0][1].lineNumber()).isEqualTo(2);
		assertThat(result[1][0].lineNumber()).isEqualTo(0);
		assertThat(result[1][1].lineNumber()).isEqualTo(1);
	}

	private IRangeComparator toRangeComparator(String s) {
		IDocument doc1 = new Document();
		doc1.set(s);
		return new DocLineComparator(doc1, null, true);
	}

	private RangeDifference[] getDifferences(String s1, String s2) {
		IRangeComparator comp1 = toRangeComparator(s1);
		IRangeComparator comp2 = toRangeComparator(s2);
		RangeDifference[] differences = RangeDifferencer.findDifferences(comp1, comp2);
		RangeDifference[] oldDifferences = RangeDifferencer.findDifferences(comp1, comp2);
		assertThat(differences).containsExactly(oldDifferences);
		return differences;
	}

	@Test
	public void testDocAddition() {
		String s1 = ABC + SEPARATOR + DEF + SEPARATOR + XYZ;
		String s2 = ABC + SEPARATOR + DEF + SEPARATOR + _123 + SEPARATOR + XYZ;

		RangeDifference[] result = getDifferences(s1, s2);

		assertThat(result).hasSize(1);
		assertThat(result[0].leftStart()).isEqualTo(2);
		assertThat(result[0].leftLength()).isEqualTo(0);
		assertThat(result[0].rightStart()).isEqualTo(2);
		assertThat(result[0].rightLength()).isEqualTo(1);
	}

	@Test
	public void testDocDeletion() {
		String s1 = ABC + SEPARATOR + DEF + SEPARATOR + _123 + SEPARATOR + XYZ;
		String s2 = ABC + SEPARATOR + DEF + SEPARATOR + XYZ;

		RangeDifference[] result = getDifferences(s1, s2);

		assertThat(result).hasSize(1);
		assertThat(result[0].leftStart()).isEqualTo(2);
		assertThat(result[0].leftLength()).isEqualTo(1);
		assertThat(result[0].rightStart()).isEqualTo(2);
		assertThat(result[0].rightLength()).isEqualTo(0);
	}

	@Test
	public void testDocAppendStart() {
		String s1 = ABC + SEPARATOR + DEF;
		String s2 = _123 + SEPARATOR + ABC + SEPARATOR + DEF;

		RangeDifference[] result = getDifferences(s1, s2);

		assertThat(result).hasSize(1);
		assertThat(result[0].leftStart()).isEqualTo(0);
		assertThat(result[0].leftLength()).isEqualTo(0);
		assertThat(result[0].rightStart()).isEqualTo(0);
		assertThat(result[0].rightLength()).isEqualTo(1);
	}

	@Test
	public void testDocDeleteStart() {
		String s1 = _123 + SEPARATOR + ABC + SEPARATOR + DEF;
		String s2 = ABC + SEPARATOR + DEF;

		RangeDifference[] result = getDifferences(s1, s2);

		assertThat(result).hasSize(1);
		assertThat(result[0].leftStart()).isEqualTo(0);
		assertThat(result[0].leftLength()).isEqualTo(1);
		assertThat(result[0].rightStart()).isEqualTo(0);
		assertThat(result[0].rightLength()).isEqualTo(0);
	}

	@Test
	public void testDocAppendEnd() {
		String s1 = ABC + SEPARATOR + DEF;
		String s2 = ABC + SEPARATOR + DEF + SEPARATOR + _123;

		RangeDifference[] result = getDifferences(s1, s2);

		assertThat(result).hasSize(1);
		assertThat(result[0].leftStart()).isEqualTo(2);
		assertThat(result[0].leftLength()).isEqualTo(0);
		assertThat(result[0].rightStart()).isEqualTo(2);
		assertThat(result[0].rightLength()).isEqualTo(1);
	}

	@Test
	public void testDocDeleteEnd() {
		String s1 = ABC + SEPARATOR + DEF + SEPARATOR + _123;
		String s2 = ABC + SEPARATOR + DEF;

		RangeDifference[] result = getDifferences(s1, s2);

		assertThat(result).hasSize(1);
		assertThat(result[0].leftStart()).isEqualTo(2);
		assertThat(result[0].leftLength()).isEqualTo(1);
		assertThat(result[0].rightStart()).isEqualTo(2);
		assertThat(result[0].rightLength()).isEqualTo(0);
	}

}
