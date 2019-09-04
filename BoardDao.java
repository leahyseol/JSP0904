package com.exam.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.exam.vo.BoardVO;

public class BoardDao {
	
	private static BoardDao instance = new BoardDao();

	public static BoardDao getInstance() {
		return instance;
	}

	private BoardDao() {
	}
	
	// insert할 레코드의 번호 생성 메소드
	public int nextBoardNum() {
		int num = 0;
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			con = DBManager.getConnection();
			// num 컬럼값중에 최대값 구하기. 레코드 없으면 null
			String sql = "SELECT MAX(num) FROM board";
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			// rs 레코드값 있으면 num = 최대값 + 1
			//             없으면 num = 1
			if (rs.next()) {
				num = rs.getInt(1) + 1;
			} else {
				num = 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBManager.close(con, stmt, rs);
		}
		return num;
	} // nextBoardNum method
	
	
	// 게시글 한개 등록하는 메소드
	public void insertBoard(BoardVO boardVO) {
		Connection con = null;
		PreparedStatement pstmt = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			con = DBManager.getConnection();
			
			sb.append("INSERT INTO board (num, username, passwd, subject, content, filename, readcount, ip, reg_date, re_ref, re_lev, re_seq) ");
			sb.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			pstmt = con.prepareStatement(sb.toString());
			pstmt.setInt(1, boardVO.getNum());
			pstmt.setString(2, boardVO.getUsername());
			pstmt.setString(3, boardVO.getPasswd());
			pstmt.setString(4, boardVO.getSubject());
			pstmt.setString(5, boardVO.getContent());
			pstmt.setString(6, boardVO.getFilename());
			pstmt.setInt(7, boardVO.getReadcount());
			pstmt.setString(8, boardVO.getIp());
			pstmt.setTimestamp(9, boardVO.getRegDate());
			pstmt.setInt(10, boardVO.getRe_ref());
			pstmt.setInt(11, boardVO.getRe_lev());
			pstmt.setInt(12, boardVO.getRe_seq());
			// 실행
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBManager.close(con, pstmt);
		}
	} // insertBoard method
	
/*
	// 시작행번호부터 갯수만큼 가져오기(페이징)
	public List<BoardVO> getBoards(int startRow, int pageSize) {
		List<BoardVO> list = new ArrayList<BoardVO>();
		int endRow = startRow + pageSize - 1; // 오라클전용 끝행번호
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT aa.* ");
		sb.append("FROM ");
		sb.append("    (SELECT ROWNUM AS rnum, a.* ");
		sb.append("    FROM ");
		sb.append("        (SELECT * ");
		sb.append("        FROM board ");
		sb.append("        ORDER BY num DESC) a ");
		sb.append("    WHERE ROWNUM <= ?) aa ");
		sb.append("WHERE rnum >= ? ");
		
		try {
			con = DBManager.getConnection();
			pstmt = con.prepareStatement(sb.toString());
			pstmt.setInt(1, endRow);
			pstmt.setInt(2, startRow);
			// 실행
			rs = pstmt.executeQuery();
			while (rs.next()) {
				BoardVO boardVO = new BoardVO();
				boardVO.setNum(rs.getInt("num"));
				boardVO.setUsername(rs.getString("username"));
				boardVO.setPasswd(rs.getString("passwd"));
				boardVO.setSubject(rs.getString("subject"));
				boardVO.setContent(rs.getString("content"));
				boardVO.setFilename(rs.getString("filename"));
				boardVO.setReadcount(rs.getInt("readcount"));
				boardVO.setIp(rs.getString("ip"));
				boardVO.setRegDate(rs.getTimestamp("reg_date"));
				boardVO.setRe_ref(rs.getInt("re_ref"));
				boardVO.setRe_lev(rs.getInt("re_lev"));
				boardVO.setRe_seq(rs.getInt("re_seq"));
				// 리스트에 vo객체 한개 추가
				list.add(boardVO);
			} // while
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBManager.close(con, pstmt, rs);
		}
		return list;
	} // getBoards method
	
	
	// 검색어로 검색된 행의 시작행번호부터 갯수만큼 가져오기(페이징)
	public List<BoardVO> getBoards(int startRow, int pageSize, String search) {
		List<BoardVO> list = new ArrayList<BoardVO>();
		int endRow = startRow + pageSize - 1; // 오라클전용 끝행번호
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT aa.* ");
		sb.append("FROM ");
		sb.append("    (SELECT ROWNUM AS rnum, a.* ");
		sb.append("    FROM ");
		sb.append("        (SELECT * ");
		sb.append("        FROM board ");
		sb.append("        WHERE subject LIKE ? ");
		sb.append("        ORDER BY num DESC) a ");
		sb.append("    WHERE ROWNUM <= ?) aa ");
		sb.append("WHERE rnum >= ? ");
		
		try {
			con = DBManager.getConnection();
			pstmt = con.prepareStatement(sb.toString());
			pstmt.setString(1, "%" + search + "%");
			pstmt.setInt(2, endRow);
			pstmt.setInt(3, startRow);
			// 실행
			rs = pstmt.executeQuery();
			while (rs.next()) {
				BoardVO boardVO = new BoardVO();
				boardVO.setNum(rs.getInt("num"));
				boardVO.setUsername(rs.getString("username"));
				boardVO.setPasswd(rs.getString("passwd"));
				boardVO.setSubject(rs.getString("subject"));
				boardVO.setContent(rs.getString("content"));
				boardVO.setFilename(rs.getString("filename"));
				boardVO.setReadcount(rs.getInt("readcount"));
				boardVO.setIp(rs.getString("ip"));
				boardVO.setRegDate(rs.getTimestamp("reg_date"));
				boardVO.setRe_ref(rs.getInt("re_ref"));
				boardVO.setRe_lev(rs.getInt("re_lev"));
				boardVO.setRe_seq(rs.getInt("re_seq"));
				// 리스트에 vo객체 한개 추가
				list.add(boardVO);
			} // while
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBManager.close(con, pstmt, rs);
		}
		return list;
	} // getBoards method
*/
	
	
	public List<BoardVO> getBoards(int startRow, int pageSize, String search) {
		List<BoardVO> list = new ArrayList<BoardVO>();
		int endRow = startRow + pageSize - 1; // 오라클전용 끝행번호
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT aa.* ");
		sb.append("FROM ");
		sb.append("    (SELECT ROWNUM AS rnum, a.* ");
		sb.append("    FROM ");
		sb.append("        (SELECT * ");
		sb.append("        FROM board ");
		
		// 검색어 search가 있을때는 검색조건절 where를 추가함
		if (!(search == null || search.equals(""))) {
			sb.append("        WHERE subject LIKE ? ");
		}
		
		sb.append("        ORDER BY num DESC) a ");
		sb.append("    WHERE ROWNUM <= ?) aa ");
		sb.append("WHERE rnum >= ? ");
		
		try {
			con = DBManager.getConnection();
			pstmt = con.prepareStatement(sb.toString());
			
			if (!(search == null || search.equals(""))) {
				// 검색어가 있을때
				pstmt.setString(1, "%" + search + "%");
				pstmt.setInt(2, endRow);
				pstmt.setInt(3, startRow);
			} else {
				// 검색어가 없을때
				pstmt.setInt(1, endRow);
				pstmt.setInt(2, startRow);
			}
			
			// 실행
			rs = pstmt.executeQuery();
			while (rs.next()) {
				BoardVO boardVO = new BoardVO();
				boardVO.setNum(rs.getInt("num"));
				boardVO.setUsername(rs.getString("username"));
				boardVO.setPasswd(rs.getString("passwd"));
				boardVO.setSubject(rs.getString("subject"));
				boardVO.setContent(rs.getString("content"));
				boardVO.setFilename(rs.getString("filename"));
				boardVO.setReadcount(rs.getInt("readcount"));
				boardVO.setIp(rs.getString("ip"));
				boardVO.setRegDate(rs.getTimestamp("reg_date"));
				boardVO.setRe_ref(rs.getInt("re_ref"));
				boardVO.setRe_lev(rs.getInt("re_lev"));
				boardVO.setRe_seq(rs.getInt("re_seq"));
				// 리스트에 vo객체 한개 추가
				list.add(boardVO);
			} // while
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBManager.close(con, pstmt, rs);
		}
		return list;
	} // getBoards method
	

/*
	// 게시판(board) 테이블 레코드 개수 가져오기 메소드
	public int getBoardCount() {
		int count = 0;
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			con = DBManager.getConnection();
			String sql = "SELECT COUNT(*) FROM board";
			stmt = con.createStatement();
			// 실행
			rs = stmt.executeQuery(sql);
			
			rs.next(); // 커서 옮겨서 행 존재유무 true/false 리턴
			
			count = rs.getInt(1); // 행개수 count변수에 저장
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBManager.close(con, stmt, rs);
		}
		return count;
	} // getBoardCount method
	
	
	public int getBoardCount(String search) {
		int count = 0;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			con = DBManager.getConnection();
			String sql = "SELECT COUNT(*) FROM board ";
			sql += "WHERE subject LIKE ? ";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, "%"+search+"%");
			// 실행
			rs = pstmt.executeQuery(sql);
			
			rs.next(); // 커서 옮겨서 행 존재유무 true/false 리턴
			
			count = rs.getInt(1); // 행개수 count변수에 저장
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBManager.close(con, pstmt, rs);
		}
		return count;
	} // getBoardCount method
*/
	
	
	public int getBoardCount(String search) {
		int count = 0;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			con = DBManager.getConnection();
			String sql = "SELECT COUNT(*) FROM board ";
			
			// 동적(Dynamic) SQL
			if (!(search == null || search.equals(""))) {
				sql += "WHERE subject LIKE ? ";
			}
			
			pstmt = con.prepareStatement(sql);
			
			
			if (!(search == null || search.equals(""))) {
				pstmt.setString(1, "%"+search+"%");
			}
			
			// 실행
			rs = pstmt.executeQuery(sql);
			
			rs.next(); // 커서 옮겨서 행 존재유무 true/false 리턴
			
			count = rs.getInt(1); // 행개수 count변수에 저장
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBManager.close(con, pstmt, rs);
		}
		return count;
	} // getBoardCount method
	
	
	// 특정 레코드의 조회수를 1 증가시키는 메소드
	public void updateReadcount(int num) {
		Connection con = null;
		PreparedStatement pstmt = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			con = DBManager.getConnection();
			sb.append("UPDATE board ");
			sb.append("SET readcount = readcount + 1 ");
			sb.append("WHERE num = ?");
			
			pstmt = con.prepareStatement(sb.toString());
			pstmt.setInt(1, num);
			// 실행
			pstmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBManager.close(con, pstmt);
		}
	} // updateReadcount method
	
	
	// 글 한개를 가져오는 메소드
	public BoardVO getBoard(int num) {
		BoardVO boardVO = null;
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			con = DBManager.getConnection();
			String sql = "SELECT * FROM board WHERE num = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, num);
			// 실행
			rs = pstmt.executeQuery();
			if (rs.next()) {
				boardVO = new BoardVO();
				boardVO.setNum(rs.getInt("num"));
				boardVO.setUsername(rs.getString("username"));
				boardVO.setPasswd(rs.getString("passwd"));
				boardVO.setSubject(rs.getString("subject"));
				boardVO.setContent(rs.getString("content"));
				boardVO.setFilename(rs.getString("filename"));
				boardVO.setReadcount(rs.getInt("readcount"));
				boardVO.setIp(rs.getString("ip"));
				boardVO.setRegDate(rs.getTimestamp("reg_date"));
				boardVO.setRe_ref(rs.getInt("re_ref"));
				boardVO.setRe_lev(rs.getInt("re_lev"));
				boardVO.setRe_seq(rs.getInt("re_seq"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBManager.close(con, pstmt, rs);
		}
		return boardVO;
	} // getBoard method
	
	
	// 게시글 패스워드비교(로그인 안한 사용자가 수행함)
	public boolean isPasswdEqual(int num, String passwd) {
		System.out.println("num : " + num + ", passwd : " + passwd);
		
		boolean result = false;
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT COUNT(*) ");
		sb.append("FROM board ");
		sb.append("WHERE num = ? ");
		sb.append("AND passwd = ? ");
		
		try {
			con = DBManager.getConnection();
			pstmt = con.prepareStatement(sb.toString());
			pstmt.setInt(1, num);
			pstmt.setString(2, passwd);
			// 실행
			rs = pstmt.executeQuery();
			
			rs.next(); // 커서 내리기
			
			int count = rs.getInt(1); // 카운트값 가져오기
			if (count == 1) {
				result = true; // 게시글 패스워드 같음
			} else { // count == 0
				result = false; // 게시글 패스워드 다름
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBManager.close(con, pstmt, rs);
		}
		return result;
	} // isPasswdEqual method
	
	
	// 게시글 수정하기 메소드
	public void updateBoard(BoardVO boardVO) {
		Connection con = null;
		PreparedStatement pstmt = null;
		
		String sql = "";
		sql  = "UPDATE board ";
		sql += "SET subject = ?, content = ? ";
		sql += "WHERE num = ? ";
		
		try {
			con = DBManager.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, boardVO.getSubject());
			pstmt.setString(2, boardVO.getContent());
			pstmt.setInt(3, boardVO.getNum());
			// 실행
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBManager.close(con, pstmt);
		}
	} // updateBoard method
	
	
	// 글번호에 해당하는 글 한개 삭제하기 메소드
	public void deleteBoard(int num) {
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try {
			con = DBManager.getConnection();
			String sql = "DELETE FROM board WHERE num = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, num);
			// 실행
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBManager.close(con, pstmt);
		}
	} // deleteBoard method
	
	
	
	
	
}
