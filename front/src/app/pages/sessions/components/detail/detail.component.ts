import { Component, inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { BehaviorSubject, Observable, shareReplay, switchMap, tap } from 'rxjs';
import { Teacher } from '../../../../core/models/teacher.interface';
import { SessionService } from '../../../../core/service/session.service';
import { TeacherService } from '../../../../core/service/teacher.service';
import { Session } from '../../../../core/models/session.interface';
import { SessionApiService } from '../../../../core/service/session-api.service';
import { MaterialModule } from "../../../../shared/material.module";
import { CommonModule } from "@angular/common";

@Component({
  selector: 'app-detail',
  imports: [CommonModule, MaterialModule],
  templateUrl: './detail.component.html',
  styleUrls: ['./detail.component.scss']
})
export class DetailComponent {
  public isParticipate = false;
  public isAdmin = false;

  private route = inject(ActivatedRoute);
  private sessionService = inject(SessionService);
  private sessionApiService = inject(SessionApiService);
  private teacherService = inject(TeacherService);
  private matSnackBar = inject(MatSnackBar);
  private router = inject(Router);
  private refreshSession$ = new BehaviorSubject<void>(undefined);

  public sessionId: string = this.route.snapshot.paramMap.get('id')!;
  public userId: string = this.sessionService.sessionInformation!.id.toString();

  public session$: Observable<Session>;
  public teacher$: Observable<Teacher>;

  constructor() {
    this.isAdmin = this.sessionService.sessionInformation!.admin;

    this.session$ = this.refreshSession$.pipe(
      switchMap(() => this.sessionApiService.detail(this.sessionId)),
      tap((session: Session) => {
        this.isParticipate = session.users.some(u => u === this.sessionService.sessionInformation!.id);
      }),
      shareReplay(1)
    );

    this.teacher$ = this.session$.pipe(
      switchMap((session: Session) => this.teacherService.detail(session.teacher_id.toString())),
      shareReplay(1)
    );
  }

  public back(): void {
    window.history.back();
  }

  public delete(): void {
    this.sessionApiService
      .delete(this.sessionId)
      .subscribe(() => {
        this.matSnackBar.open('Session deleted !', 'Close', { duration: 3000 });
        this.router.navigate(['sessions']);
      });
  }

  public participate(): void {
    this.sessionApiService.participate(this.sessionId, this.userId)
      .subscribe(() => this.refreshSession$.next());
  }

  public unParticipate(): void {
    this.sessionApiService.unParticipate(this.sessionId, this.userId)
      .subscribe(() => this.refreshSession$.next());
  }

}
