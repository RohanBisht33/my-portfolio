"use strict";
/* ═══════════════════════════════════════════════════════════
   PORTFOLIO.JS — Unified Frontend Logic (Blog-Free)
   Project CRUD, Admin Auth, Modal System, Resume Handler
═══════════════════════════════════════════════════════════ */

const API = '/api';

/* ─── Helpers ───────────────────────────────────────────── */
function escHtml(s){return String(s).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');}
function formatDate(d){if(!d)return'Unknown';try{return new Date(d).toLocaleDateString('en-US',{year:'numeric',month:'long',day:'numeric'});}catch{return'Invalid date';}}

/* Toast (reuses #toast element from main page) */
const Toast={_t:null,show(msg,dur=2400){const el=document.getElementById('toast');if(!el)return;clearTimeout(Toast._t);el.textContent=msg;el.classList.add('show');Toast._t=setTimeout(()=>el.classList.remove('show'),dur);}};

/* ─── Modal System ──────────────────────────────────────── */
function openModal(id){const m=document.getElementById(id);if(m){m.classList.add('open');document.body.style.overflow='hidden';}}
function closeModal(id){const m=document.getElementById(id);if(m){m.classList.remove('open');document.body.style.overflow='';}}

document.addEventListener('click',e=>{
    const c=e.target.closest('[data-modal-close]');
    if(c){closeModal(c.dataset.modalClose);return;}
    if(e.target.classList.contains('modal-overlay'))closeModal(e.target.id);
});
document.addEventListener('keydown',e=>{if(e.key==='Escape')document.querySelectorAll('.modal-overlay.open').forEach(m=>closeModal(m.id));});

/* ─── Mobile Nav ────────────────────────────────────────── */
document.getElementById('hamburger-btn')?.addEventListener('click',()=>document.getElementById('mobile-nav')?.classList.add('open'));
document.getElementById('mobile-nav-close')?.addEventListener('click',()=>document.getElementById('mobile-nav')?.classList.remove('open'));
document.querySelectorAll('.mobile-nav-link').forEach(a=>a.addEventListener('click',()=>document.getElementById('mobile-nav')?.classList.remove('open')));

/* ─── Admin Auth & Visibility ────────────────────────── */
let adminToken = localStorage.getItem('adminToken') || null;
let pendingAdminAction = null;

window.openModal = openModal;
window.closeModal = closeModal;

window.updateAdminVisibility = function(){
    adminToken = localStorage.getItem('adminToken') || null;
    const isAdmin = !!adminToken;
    document.querySelectorAll('.admin-only').forEach(el=>{
        el.style.display = isAdmin ? '' : 'none';
    });
};

// Initialize visibility immediately and on DOM load
window.updateAdminVisibility();
document.addEventListener('DOMContentLoaded', window.updateAdminVisibility);

function requireAuth(callback){
    adminToken = localStorage.getItem('adminToken') || null;
    if(adminToken){callback();return;}
    pendingAdminAction=callback;
    Toast.show('Type \'login imrb rb@123\' in terminal to unlock Add Project', 3500);
}

document.getElementById('auth-submit')?.addEventListener('click',async()=>{
    const pw=document.getElementById('auth-password')?.value;
    if(!pw){Toast.show('Enter password');return;}
    try{
        const r=await fetch(`${API}/auth/login`,{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({username:'imrb',password:pw})});
        if(r.ok){
            const d=await r.json();
            adminToken=d.token||'adm_session';localStorage.setItem('adminToken',adminToken);
            window.updateAdminVisibility();
            document.getElementById('auth-overlay')?.classList.remove('open');
            Toast.show('Authenticated ✓');if(pendingAdminAction)pendingAdminAction();
        } else {
            Toast.show('Invalid password — use terminal: login imrb rb@123',3000);
        }
    }catch{
        Toast.show('Auth failed — use terminal: login imrb rb@123',3000);
    }
});
document.getElementById('auth-cancel')?.addEventListener('click',()=>{document.getElementById('auth-overlay')?.classList.remove('open');pendingAdminAction=null;});
document.getElementById('auth-password')?.addEventListener('keydown',e=>{if(e.key==='Enter')document.getElementById('auth-submit')?.click();});

function authHeaders(){return adminToken?{'Authorization':`Bearer ${adminToken}`}:{};}
function logout(){localStorage.removeItem('adminToken');adminToken=null;window.updateAdminVisibility();Toast.show('Logged out');}

/* ═══════════════════════════════════════════════════════════
   MODULE J: ADD PROJECT BUTTONS — Wire up all triggers
═══════════════════════════════════════════════════════════ */
(function initAddProjectButtons(){
    // All "Add Project" buttons open the modal with auth
    ['btn-add-project','btn-add-project-section'].forEach(id=>{
        document.getElementById(id)?.addEventListener('click',()=>requireAuth(()=>openModal('modal-add-project')));
    });
})();


/* ═══════════════════════════════════════════════════════════
   MODULE K: ADD PROJECT FORM (Admin)
═══════════════════════════════════════════════════════════ */
(function initAddProject(){
    const form=document.getElementById('add-project-form');
    if(!form)return;
    const fileInput=document.getElementById('proj-image');
    const preview=document.getElementById('proj-preview-img');
    const dropZone=document.getElementById('proj-drop-zone');
    const descEl=document.getElementById('proj-desc');
    const charCount=document.getElementById('proj-char-count');

    descEl?.addEventListener('input',()=>{if(charCount)charCount.textContent=descEl.value.length;});

    fileInput?.addEventListener('change',()=>{
        const f=fileInput.files[0];if(!f)return;
        if(f.size>5*1024*1024){Toast.show('Image must be < 5MB');fileInput.value='';return;}
        const rd=new FileReader();rd.onload=e=>{preview.src=e.target.result;preview.style.display='block';};rd.readAsDataURL(f);
    });

    ['dragover','dragleave','drop'].forEach(ev=>dropZone?.addEventListener(ev,e=>{
        e.preventDefault();
        if(ev==='dragover')dropZone.classList.add('drag-over');
        else dropZone.classList.remove('drag-over');
        if(ev==='drop'&&e.dataTransfer.files.length){fileInput.files=e.dataTransfer.files;fileInput.dispatchEvent(new Event('change'));}
    }));

    form.addEventListener('submit',async e=>{
        e.preventDefault();
        const title=document.getElementById('proj-title').value.trim();
        const desc=descEl.value.trim();
        if(!title){Toast.show('Title required');return;}
        if(desc.length<10){Toast.show('Description too short');return;}

        const fd=new FormData();
        fd.append('title',title);fd.append('description',desc);
        fd.append('status',document.getElementById('proj-status').value);
        fd.append('featured',document.getElementById('proj-featured').checked);
        if(fileInput.files[0])fd.append('image',fileInput.files[0]);
        const tech=document.getElementById('proj-tech').value.trim();if(tech)fd.append('technologies',tech);
        const gh=document.getElementById('proj-github').value.trim();if(gh)fd.append('githubUrl',gh);
        const lv=document.getElementById('proj-live').value.trim();if(lv)fd.append('liveUrl',lv);

        const btn=document.getElementById('proj-submit-btn');
        btn.classList.add('loading');btn.textContent='Adding...';
        try{
            const r=await fetch(`${API}/projects`,{method:'POST',headers:authHeaders(),body:fd});
            if(r.ok){Toast.show('Project added ✓');form.reset();preview.style.display='none';closeModal('modal-add-project');setTimeout(()=>{window.location.href='/';},800);}
            else{const d=await r.json().catch(()=>({}));Toast.show(d.error||'Failed to add project',3000);}
        }catch(err){Toast.show('Failed: '+err.message,3000);}
        finally{btn.classList.remove('loading');btn.textContent='Add Project';}
    });
})();


/* ═══════════════════════════════════════════════════════════
   MODULE M: PROJECT DETAIL MODAL
═══════════════════════════════════════════════════════════ */
(function initProjectDetail(){
    document.addEventListener('click',async e=>{
        const card=e.target.closest('.project-card[data-project-id]');
        if(!card||e.target.closest('.engagement-bar')||e.target.closest('.comment-panel'))return;
        const id=card.dataset.projectId;
        try{
            const r=await fetch(`${API}/projects/${id}`);
            if(!r.ok)throw new Error(r.status);
            const p=await r.json();
            const statusClass=p.status==='completed'?'status-completed':p.status==='in-progress'?'status-in-progress':'status-planning';
            const statusText=(p.status||'completed').replace(/-/g,' ').replace(/\b\w/g,l=>l.toUpperCase());
            document.getElementById('project-detail-content').innerHTML=`
                ${p.image?`<img class="project-detail-image" src="${p.image}" alt="${escHtml(p.title)}" onerror="this.style.display='none'">`:''}
                <div style="display:flex;justify-content:space-between;align-items:center;flex-wrap:wrap;gap:1rem;margin-bottom:1rem;">
                    <h2 style="font-family:var(--font-display);font-size:1.6rem;font-weight:700;">${escHtml(p.title)}</h2>
                    <span class="project-detail-status ${statusClass}">${statusText}</span>
                </div>
                <p style="color:var(--text-muted);line-height:1.8;margin-bottom:1.5rem;">${escHtml(p.description||'').replace(/\n/g,'<br>')}</p>
                ${p.techStack?.length?`<div style="display:flex;flex-wrap:wrap;gap:0.4rem;margin-bottom:1.5rem;">${p.techStack.map(t=>`<span class="tag tag-cyan">${escHtml(t)}</span>`).join('')}</div>`:''}
                ${p.liveUrl?`<div style="margin:1.5rem 0;border:1px solid var(--border);border-radius:var(--radius-lg);overflow:hidden;"><div style="padding:0.5rem 1rem;background:rgba(0,0,0,0.3);font-size:0.75rem;color:var(--text-dim);">🌐 Live Preview — ${escHtml(p.liveUrl)}</div><iframe src="${p.liveUrl}" style="width:100%;height:400px;border:none;" sandbox="allow-scripts allow-same-origin" loading="lazy"></iframe></div>`:''}
                <div class="project-detail-links">
                    ${p.githubUrl?`<a href="${p.githubUrl}" target="_blank" rel="noopener" class="btn btn-cyan">GitHub</a>`:''}
                    ${p.liveUrl?`<a href="${p.liveUrl}" target="_blank" rel="noopener" class="btn btn-purple">Live Demo</a>`:''}
                </div>`;
            openModal('modal-project-detail');
        }catch(err){console.error(err);Toast.show('Failed to load project',3000);}
    });
})();


/* ═══════════════════════════════════════════════════════════
   MODULE N: DOWNLOAD RESUME HANDLER
═══════════════════════════════════════════════════════════ */
document.getElementById('btn-download-resume')?.addEventListener('click',e=>{
    // Smoothly initiate download
    Toast.show('Downloading Rohan Bisht\'s Resume...');
});
