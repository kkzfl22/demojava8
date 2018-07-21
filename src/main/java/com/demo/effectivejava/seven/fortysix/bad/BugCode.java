package com.demo.effectivejava.seven.fortysix.bad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.demo.effectivejava.seven.fortysix.bad.BugCode.RANK;
import com.demo.effectivejava.seven.fortysix.bad.BugCode.Suit;

class Card {
	private Suit suit;
	private RANK rand;

	public Card(Suit suit, RANK rand) {
		super();
		this.suit = suit;
		this.rand = rand;
	}

	public Suit getSuit() {
		return suit;
	}

	public void setSuit(Suit suit) {
		this.suit = suit;
	}

	public RANK getRand() {
		return rand;
	}

	public void setRand(RANK rand) {
		this.rand = rand;
	}
}

public class BugCode {

	enum Suit {
		CLUB, DIAMOND, HEART, SPADE
	};

	enum RANK {
		ACE, DEUCE, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACk, QUEEN, KING
	};

	public void testPrint() {
		Collection<Suit> suits = Arrays.asList(Suit.values());
		Collection<RANK> ranks = Arrays.asList(RANK.values());

		List<Card> dec = new ArrayList<>();

		// for (Iterator<Suit> i = suits.iterator(); i.hasNext();) {
		// for (Iterator<RANK> j = ranks.iterator(); j.hasNext();) {
		// dec.add(new Card(i.next(), j.next()));
		// }
		// }

		// 解决办法
		for (Iterator<Suit> i = suits.iterator(); i.hasNext();) {
			Suit iu = i.next();
			for (Iterator<RANK> j = ranks.iterator(); j.hasNext();) {
				dec.add(new Card(iu, j.next()));
			}
		}

		System.out.println(dec.size());
	}

	public static void main(String[] args) {
		BugCode bad = new BugCode();
		bad.testPrint();
	}

}
