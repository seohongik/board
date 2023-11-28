package com.wings.board.dto;

import lombok.*;
import org.apache.ibatis.type.Alias;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardPageDTO {

	private int realEnd;
	private int startPage;
	private int endPage;
	private boolean prev, next;

	private int total;
	private int pageNum;
	private int amount;
	private int currentPage;

	private int numMutiAmount;
	private int numMinusOneMutiAmount;

	public BoardPageDTO(int pageNum, int amount, int total) {
		this.pageNum = pageNum;
		this.amount = amount;
		this.total = total;
		this.endPage = (int) (Math.ceil(this.pageNum / 10.0)) * 10; // 10
		this.startPage = this.endPage - 9;// 1
		this.currentPage = pageNum;
		this.realEnd = (int) (Math.ceil((this.total * 1.0) / amount));
		if (this.endPage > this.realEnd) {
			this.endPage = this.realEnd;
		}
		this.prev = this.startPage > 1;
		this.next = this.endPage < this.realEnd;
	}

	public int calcPageNum(int rn) {
		int calcPageNum = (int)Math.ceil(rn/10)+1;

		if(rn%10==0) {
			return (calcPageNum-1);
		}
				
		return calcPageNum;
	}

}
