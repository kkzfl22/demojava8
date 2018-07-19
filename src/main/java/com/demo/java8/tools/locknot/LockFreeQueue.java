package com.demo.java8.tools.locknot;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class LockFreeQueue<V> {

	@SuppressWarnings("hiding")
	private class Node<V> {
		/**
		 * 当前节点
		 */
		private V value = null;
		/**
		 * 下一个节点
		 */
		private AtomicReference<Node<V>> next = null;

		public Node(V value, Node<V> next) {
			this.value = value;
			this.next = new AtomicReference<Node<V>>(next);
		}
	}

	/**
	 * 首节点
	 */
	private AtomicReference<Node<V>> head = null;

	/**
	 * 最后一个节点
	 */
	private AtomicReference<Node<V>> tail = null;

	/**
	 * 队列长度
	 */
	private AtomicInteger queueSize = new AtomicInteger(0);

	public LockFreeQueue() {
		// 初始化让两个节点，指向一个空节点
		Node<V> node = new Node<V>(null, null);
		head = new AtomicReference<Node<V>>(node);
		tail = new AtomicReference<Node<V>>(node);
	}

	/**
	 * 进行队列的添加操作
	 */
	public void addQueue(V value) {
		// 将当前值做封装
		Node<V> nodeValue = new Node<V>(value, null);

		Node<V> oldNode = null;
		AtomicReference<Node<V>> nextNode = null;
		while (true) {
			oldNode = tail.get();
			// 下一个节点
			nextNode = oldNode.next;
			// 如果下一个节点未指定，则直接设置为新的节点
			if (nextNode.compareAndSet(null, nodeValue)) {
				break;
			}
			// 已经指向新节点，则更新为下一个节点
			else {
				tail.compareAndSet(oldNode, oldNode.next.get());
			}
		}
		// 进行自加操作
		queueSize.getAndIncrement();
		// 将下一个节点指向未节点
		tail.compareAndSet(oldNode, oldNode.next.get());
	}

	/**
	 * 获取队列的首个元素
	 * 
	 * @return
	 */
	public V getQueue() {
		while (true) {
			Node<V> oldHead = head.get();
			// 首节点的下一个节点
			AtomicReference<Node<V>> next = oldHead.next;
			
			//如果下一他节点为空，则返回
			if (next.get() == null) {
				return null;
			}
			
			//将首节点更新为下一个节点
			if (head.compareAndSet(oldHead, oldHead.next.get())) {
				queueSize.getAndDecrement();
				return oldHead.next.get().value;
			}

		}
	}

	public int getSize() {
		return queueSize.get();
	}

	public static void main(String[] args) {
		LockFreeQueue<String> queue = new LockFreeQueue<>();

		new Thread(new Runnable() {
			public void run() {
				while (true) {
					queue.addQueue(String.valueOf(ThreadLocalRandom.current().nextInt(900000)));
				}
			}
		}).start();
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					String value = queue.getQueue();

					if (null != value) {
						System.out.println(value);
					}
				}
			}
		}).start();

	}

}
