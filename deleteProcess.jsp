<%@page import="com.exam.dao.BoardDao"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%--bring session (로그인 여부 확인)--%>
<% String id=(String)session.getAttribute("id"); %>

<%
//post parameter value -> Korean version
request.setCharacterEncoding("utf-8");

//parameter 저장 할 변수 선언
String pageNum=request.getParameter("pageNum");
int num=Integer.parseInt(request.getParameter("num"));
//passwd parameter value -> 로그인 사용자일 경우는 null
String passwd=request.getParameter("passwd");

//로그인 안 한 사용자는 입력한 패스워드와 글패스워드를 비교새허
//같을때는 삭제처리
//다를때는 "글 패스워드 다름" 뒤로가기
if(id==null){
	if(! boardDao.isPasswdEqual(num,passwd)){
		%>
		<script>
		alert('wrong passwd');
		history.back();
		</script>
		<%
		return;
	}
}

//게시글 삭제하기 메소드 호출
//[로그인 안한 사용자] 또는 [로그인 안 한 사용자인데 비밀번호 일치 한 사람]
		boardDao.deleteBoard(num);

//삭제처리 후 글 목록 notice.jsp 이동

%>
<script>
alert('글 삭제 성공!')
location.href='notice.jsp?pageNum=<%=pageNum %>';
</script>