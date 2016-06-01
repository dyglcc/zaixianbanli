package qfpay.wxshop.data.beans;



public class NoticeUnReadResponseWrapper extends CommonJsonBean {
	private static final long serialVersionUID = 1L;
	private DataWrapper data;

	public DataWrapper getData() {
		return data;
	}

	public void setData(DataWrapper data) {
		this.data = data;
	}

	public class DataWrapper {

		private int unread;

		public int getUnread() {
			return unread;
		}

		public void setUnread(int unread) {
			this.unread = unread;
		}
	}

}

