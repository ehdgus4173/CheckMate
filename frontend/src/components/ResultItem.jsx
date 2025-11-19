import { useLocation, useNavigate } from "react-router-dom";
import { useState } from "react";
import { downloadReport } from "../api/checkMateApi.js";
import "./ResultPage.css";

function ResultPage() {
    const { state } = useLocation();
    const navigate = useNavigate();
    const [selected, setSelected] = useState(null);

    if (!state || !state.summary) {
        return <p>결과 데이터가 없습니다. 다시 분석해주세요.</p>;
    }

    // 🔥 백엔드 summary 구조 기준으로 수정
    const { summary, requirementsFile, submissionFile } = state;

    // 🔥 백엔드는 summary.details 로 결과를 보냄
    const results = summary.details ?? [];

    const convertStatus = (status) => {
        switch (status) {
            case "FULFILLED": return "충족";
            case "PARTIAL": return "부분 충족";
            case "NOT_FULFILLED": return "미충족";
            default: return status;
        }
    };

    // 🔥 점수 계산 로직 - unfulfilled → notFulfilled 로 수정
    const total = summary.fulfilled + summary.partial + summary.notFulfilled;
    const finalScore = total > 0
        ? ((summary.fulfilled + summary.partial * 0.5) / total) * 100
        : 0;

    const handleDownload = async () => {
        try {
            const blob = await downloadReport(requirementsFile, submissionFile);
            const url = URL.createObjectURL(blob);

            const a = document.createElement("a");
            a.href = url;
            a.download = "CheckMate_Report.txt";
            document.body.appendChild(a);
            a.click();
            a.remove();
            URL.revokeObjectURL(url);

        } catch (e) {
            alert("보고서 다운로드 중 오류 발생");
        }
    };

    return (
        <div className="result-container">

            <h1 className="result-title">📝 분석 결과</h1>

            <div className="summary-card">
                <div className="summary-score">
                    {finalScore.toFixed(1)}점
                </div>

                <div className="summary-stats">
                    <div><b>충족:</b> {summary.fulfilled}</div>
                    <div><b>부분 충족:</b> {summary.partial}</div>

                    {/* 🔥 수정됨: unfulfilled → notFulfilled */}
                    <div><b>미충족:</b> {summary.notFulfilled}</div>
                </div>

                <button className="download-btn" onClick={handleDownload}>
                    리포트 다운로드
                </button>
            </div>

            <h2 className="detail-title">요구사항별 상세 결과</h2>

            <div className="result-list">
                {results.map((r, i) => (
                    <div
                        key={i}
                        className="result-item"
                        onClick={() =>
                            setSelected({
                                ...r,
                                // 🔥 evidence 없으면 reason 또는 “근거 없음”
                                evidence:
                                    r.evidence && r.evidence.trim() !== ""
                                        ? r.evidence
                                        : (r.reason ?? "근거 없음")
                            })
                        }
                    >
                        <b className="requirement-text">{r.requirementText}</b>

                        <span className={`status-badge status-${r.status}`}>
                            {convertStatus(r.status)}
                        </span>
                    </div>
                ))}
            </div>

            {selected && (
                <div className="modal-bg" onClick={() => setSelected(null)}>
                    <div className="modal-box" onClick={(e) => e.stopPropagation()}>
                        <h2 className="modal-title">근거 보기</h2>

                        <div className="modal-section">
                            <b>요구사항</b>
                            <p>{selected.requirementText}</p>
                        </div>

                        <div className="modal-section">
                            <b>판정</b>
                            <p>{convertStatus(selected.status)}</p>
                        </div>

                        <div className="modal-section">
                            <b>근거</b>
                            {/* 🔥 이제 evidence 항상 존재함 */}
                            <p>{selected.evidence}</p>
                        </div>

                        <button className="modal-close" onClick={() => setSelected(null)}>
                            닫기
                        </button>
                    </div>
                </div>
            )}

            <button className="restart-btn" onClick={() => navigate("/")}>
                새로 분석하기
            </button>
        </div>
    );
}

export default ResultPage;
