package com.demo.effectivejava.seven.fortysix.good;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.demo.effectivejava.seven.fortysix.bad.BugCode;
import com.demo.effectivejava.seven.fortysix.good.GoodCode.RANK;
import com.demo.effectivejava.seven.fortysix.good.GoodCode.Suit;

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

public class GoodCode {
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

		for (Suit su : suits) {
			for (RANK ran : ranks) {
				dec.add(new Card(su, ran));
			}
		}

		System.out.println(dec.size());
	}

	public static void main(String[] args) {
		BugCode bad = new BugCode();
		bad.testPrint();
	}
}
