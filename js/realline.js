document.addEventListener('DOMContentLoaded', () => {
    const videoPlayer = document.getElementById('lineVideo');
    const videoTitle = document.getElementById('videoTitle');
    const toggleBtn = document.getElementById('toggleVideoBtn');

    // 定義影片列表
    const playlist = [
        { title: '生產線', src: '../video/生產線.mp4' },
        { title: '非金屬生產線', src: '../video/非金屬生產線.mp4' }
    ];

    let currentTrack = 0;

    toggleBtn.addEventListener('click', () => {
        // 切換索引
        currentTrack = (currentTrack + 1) % playlist.length;

        // 更新影片內容
        videoPlayer.src = playlist[currentTrack].src;
        videoTitle.innerText = `當前播放：${playlist[currentTrack].title}`;

        // 切換後自動播放
        videoPlayer.play();

        // 更新按鈕提示 (選做)
        const nextTrackName = playlist[(currentTrack + 1) % playlist.length].title;
        toggleBtn.title = `切換至 ${nextTrackName}`;
    });
});